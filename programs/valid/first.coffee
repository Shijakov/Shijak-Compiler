define {
    23 >> my_age;
    'c' >> carcence;
}

fun calc(): void {
}

bag a {
    name: char[],
    age: int
}

fun main(): void {
    let dang: bag a[];
    let i: int;
    alloc bag a[10] >> dang;
    fill bag a >> dang[0];

    my_age >> eq dang[0].age;

    alloc char[5] >> dang[0].name;
    'F' >> eq dang[0].name[0];
    'I' >> eq dang[0].name[1];
    'L' >> eq dang[0].name[2];
    'I' >> eq dang[0].name[3];
    'P' >> eq dang[0].name[4];

    output 'N';
    output ' ';

    while (i < 5) {
        output dang[0].name[i];
        i + 1 >> eq i;
    }

    output ' ';
    output 'A';
    output ' ';

    output dang[0].age;
}