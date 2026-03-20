bag stringBuilder {
  string: char[],
  currIdx: int,
  strLen: int
}

fun build(builder: bag stringBuilder): char[] {
  builder.string >> return;
}

fun fil(value: char, builder: bag stringBuilder): bag stringBuilder {
  if (builder.currIdx == builder.strLen) {
    builder >> return;
  }
  value >> eq builder.string[builder.currIdx];
  builder.currIdx + 1 >> eq builder.currIdx;
  builder >> return;
}

fun createString(len: int): bag stringBuilder {
  let builder: bag stringBuilder;
  fill bag stringBuilder >> builder;
  alloc char[len] >> builder.string;
  0 >> eq builder.currIdx;
  len >> eq builder.strLen;
  builder >> return;
}

fun printString(string: char[], len: int): void {
  let i: int;
  0 >> eq i;
  while (i < len) {
    output string[i];
    i + 1 >> eq i;
  }
}

fun main(): void {
  let shijak: char[];
  createString(6)
    >> fil('S', in)
    >> fil('h', in)
    >> fil('i', in)
    >> fil('j', in)
    >> fil('a', in)
    >> fil('k', in)
    >> fil('Q', in)
    >> build(in)
    >> eq shijak;

  printString(shijak, 6);
}