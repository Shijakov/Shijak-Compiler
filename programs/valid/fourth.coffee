fun createCharArray(len: int): char[] {
    let charArr: char[];
    alloc char[len] >> charArr;
    charArr >> return;
}

fun put(charArr: char[], chr: char, idx: int): void {
    chr >> eq charArr[idx];
}

fun printRec(charArr: char[], idx: int, len: int): void {
    if (idx >= len) {
        return;
    }
    output charArr[idx];
    idx + 1 >> printRec(charArr, in, len);
}

fun print(charArr: char[], len: int): void {
    printRec(charArr, 0, len);
}

fun main (): void {
    let letters: char[];
    createCharArray(3) >> eq letters;
    put(letters, 'M', 0);
    put(letters, 'a', 1);
    put(letters, 'x', 2);

    print(letters, 3);
}