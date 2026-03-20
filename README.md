# Overview
__Shijak__ is a general-purpose programming language designed with a focus on structured programming.

The Shijak compiler takes source code written in Shijak and translates it into __MIPS assembly code__.

To test it, you can use the [online-compiler](http://64.226.66.193:3000/)

# Variables and Types

## Primitive Types
- int
- float
- char
- bool

## Complex Types
- arrays
- bags (user-defined structures)

## Variable Declaration

Variables must be declared before use:
`let identifier: TYPE;`

## Examples

### Primitive Types
```
fun main(): void {
  let i: int;
  let f: float;
  let c: char;
  let b: bool;

  5 >> eq i;
  5.5 >> eq f;
  'b' >> eq c;
  true >> eq b;

  output i;
}
```

### Arrays
```
fun main(): void {
  let arr: int[];

  alloc int[3] >> arr;

  1 >> eq arr[0];
  2 >> eq arr[1];
  3 >> eq arr[2];

  free arr;
}
```

### Bags
```
bag person {
  age: int,
  name: char[]
}

fun main(): void {
  let shijak: bag person;

  fill bag person >> shijak;

  alloc char[6] >> shijak.name;

  'S' >> eq shijak.name[0];
  'h' >> eq shijak.name[1];
  'i' >> eq shijak.name[2];
  'j' >> eq shijak.name[3];
  'a' >> eq shijak.name[4];
  'k' >> eq shijak.name[5];

  25 >> eq shijak.age;

  free shijak.name;
  free shijak;
}
```

# Operators

## Operator Precedence (highest → lowest)
1. `!` (logical NOT)
2. `*`, `/`, `&&`
3. `+`, `-`, `||`
4. `%`
5. Comparison: `>`, `<`, `>=`, `<=`, `==`, `!=`
6. `>>` (chaining operator)

# Expression Chaining (`>>`)
The `>>` operator passes the result of the left expression into the right expression via the keyword `in`.

## Example
```
fun main(): void {
  let rez: int;

  5 + 10 >> in * 2 >> in / 3 >> eq rez;

  output rez;
}
```

## Common Uses of >>

### Assignment
`5.5 >> eq num;`

### Returning a Value
```
fun add(num1: int, num2: int): int {
  num1 + num2 >> return;
}
```

### Array Allocation
`alloc int[10] >> arr;`

### Bag Initialization
`fill bag person >> shijak;`

# Statements
Shijak supports the following statements:

- Variable declaration: `let`
- Conditional statements: `if`, `elif`, `else`
- Loops: `while`
- Loop control: `break`, `continue`
- Input / Output: `input`, `output`
- Memory management: `alloc`, `free`
- Return statement: `return`
- Bag initialization: `fill`

# Control Flow

## Conditional Statements
```
fun main(): void {
  let num: int;
  input num;

  if (num < 0) {
    output -1;
  } elif (num == 0) {
    output 0;
  } else {
    output 1;
  }
}
```

## Loops
```
fun main(): void {
  let num: int;
  input num;

  while (num >= 0) {
    num - 1 >> eq num;
  }
}
```

# Functions
Functions support recursion and typed parameters.

```
fun addNumbersInArray(arr: int[], len: int): int {
  if (len == 0) {
    0 >> return;
  }

  addNumbersInArray(arr, len - 1)
    >> in + arr[len - 1]
    >> return;
}

fun main(): void {
  let arr: int[];

  alloc int[3] >> arr;

  1 >> eq arr[0];
  5 >> eq arr[1];
  3 >> eq arr[2];

  output addNumbersInArray(arr, 3);
}
```

# Input / Output
```
fun main(): void {
  let num: int;

  input num;
  output num;
}
```

# Examples
Additional examples are available in the `examples` folder located in the root directory of the project.

# Limitations
- The logical NOT operator (!) may behave inconsistently.
- Mixed-type arithmetic is not symmetric:
 - `int + float → float`
 - `float + int → int`
- Parentheses are required when using logical operators:
 - `(5 > 4) && (5 < 6)`

# Future Work
- Fix known issues
- Improve type consistency
- Add support for strings

# Remarks

The current stable implementation of the compiler is located in:
`package com.company.old`

A newer version, currently under development and based on object-oriented design principles, can be found in:
`package com.company.compiler`

