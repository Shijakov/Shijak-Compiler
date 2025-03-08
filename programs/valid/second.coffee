bag Test {
    case1: bag Case,
    case2: bag Case
}

bag Case {
    name: char,
    arr: int[]
}

fun main(): void {
    let test: bag Test;
    fill bag Test >> test;
    fill bag Case >> test.case1;
    alloc int[3] >> test.case1.arr;
    'a' >> eq test.case1.name;
    1 >> eq test.case1.arr[0];
    2 >> eq test.case1.arr[1];
    3 >> eq test.case1.arr[2];

    output test.case1.name;
    output ' ';
    output test.case1.arr[0];
    output ' ';
    output test.case1.arr[1];
    output ' ';
    output test.case1.arr[2];
}