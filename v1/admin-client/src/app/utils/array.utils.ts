import { DifferenceFilter } from "@lib";

function comparer<T>(compareArray: T[], filter: DifferenceFilter<T>) {
    return function (current: T) {
        return compareArray.filter((other: T) => {
            return filter(current, other);
        }).length === 0;
    };
}

export function arrayDifference<T>(originArray: T[], subtractArray: T[], filter: DifferenceFilter<T>): T[] {
    const onlyInOrigin = originArray.filter(comparer(subtractArray, filter));
    const onlyInSubtract = subtractArray.filter(comparer(originArray, filter));
    return onlyInOrigin.concat(onlyInSubtract);
}
