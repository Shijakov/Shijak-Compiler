bag Name {
    letters: char[],
    len: int
}

fun initializeName(letters: char[], len: int): bag Name {
    let name: bag Name;
    fill bag Name >> name;
    len >> eq name.len;
    alloc char[len] >> name.letters;
    let i: int;
    0 >> eq i;
    while (i < len) {
        letters[i] >> eq name.letters[i];
        i + 1 >> eq i;
    }

    name >> return;
}

fun printName(name: bag Name): void {
    let i: int;
    0 >> eq i;

    while (i < name.len) {
        output name.letters[i];
        i + 1 >> eq i;
    }
}

bag Person {
    name: bag Name,
    age: int
}

fun initializePerson(name: bag Name, age: int): bag Person {
    let rez: bag Person;
    fill bag Person >> rez;
    age >> eq rez.age;
    name >> eq rez.name;

    rez >> return;
}

fun printPerson(person: bag Person): void {
    printName(person.name);
    output ' ';
    output person.age;
}

fun getName(len: int): char[] {
    let name: char[];
    let i: int;
    alloc char[len] >> name;
    0 >> eq i;
    while (i < len) {
        input name[i];
        i + 1 >> eq i;
    }

    name >> return;
}

fun main (): void {
    let letters: char[];
    let age: int;
    input age;
    let nameLen: int;
    input nameLen;
    getName(nameLen) >> eq letters;
    let name: bag Name;
    initializeName(letters, nameLen) >> eq name;

    let max: bag Person;

    initializePerson(name, age) >> eq max;
    printPerson(max);
}