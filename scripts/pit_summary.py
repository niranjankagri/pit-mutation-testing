#!/usr/bin/env python3
"""Markdown summary of one test suite's run: line coverage (JaCoCo) next to the mutation score (PIT).

    python3 scripts/pit_summary.py <suite name> [target dir]

Reads target/site/jacoco/jacoco.csv and target/pit-reports/mutations.xml and prints Markdown. CI appends it to the
job summary, so the gap between "every line ran" and "a bug would be caught" is visible without opening a report.
Standard library only.
"""
import csv
import sys
import xml.etree.ElementTree as ET
from collections import Counter
from pathlib import Path


def line_coverage(target: Path) -> tuple[int, int]:
    with open(target / "site" / "jacoco" / "jacoco.csv", newline="") as f:
        rows = list(csv.DictReader(f))
    covered = sum(int(r["LINE_COVERED"]) for r in rows)
    return covered, covered + sum(int(r["LINE_MISSED"]) for r in rows)


def percent(part: int, whole: int) -> str:
    return f"{100 * part / whole:.0f}%" if whole else "n/a"


def main() -> None:
    suite = sys.argv[1]
    target = Path(sys.argv[2] if len(sys.argv) > 2 else "target")
    mutations = list(ET.parse(target / "pit-reports" / "mutations.xml").getroot())
    status = Counter(m.get("status") for m in mutations)
    covered, lines = line_coverage(target)

    print(f"### `{suite}` suite\n")
    print("| Line coverage | Mutants | Killed | Survived | No coverage | Mutation score |")
    print("|---:|---:|---:|---:|---:|---:|")
    print(f"| {percent(covered, lines)} ({covered}/{lines}) | {len(mutations)} | {status['KILLED']} "
          f"| {status['SURVIVED']} | {status['NO_COVERAGE']} | **{percent(status['KILLED'], len(mutations))}** |")

    survivors = [m for m in mutations if m.get("status") in ("SURVIVED", "NO_COVERAGE")]
    if survivors:
        print("\n<details><summary>Mutants the tests did not catch</summary>\n")
        print("| Class | Line | Change PIT made |")
        print("|---|---:|---|")
        for m in sorted(survivors, key=lambda m: (m.findtext("mutatedClass"), int(m.findtext("lineNumber")))):
            cls = m.findtext("mutatedClass").rsplit(".", 1)[-1]
            # PIT's text names the full method signature for return mutators; the class and line are enough here
            description = m.findtext("description").split(" for com/")[0]
            print(f"| `{cls}` | {m.findtext('lineNumber')} | {description} |")
        print("\n</details>")
    print()


if __name__ == "__main__":
    main()
