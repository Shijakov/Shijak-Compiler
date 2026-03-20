    bag big_number {
        value: int[],
        len: int
    }

    fun isPositive(num: bag big_number): bool {
        num.value[0] >= 0 >> return;
    }

    fun createNum(len: int): bag big_number {
        let rez: bag big_number;
        fill bag big_number >> rez;
        alloc int[len] >> rez.value;
        len >> eq rez.len;
        let i: int;
        0 >> eq i;
        while (i < len) {
            0 >> eq rez.value[i];
            i + 1 >> eq i;
        }
        rez >> return;
    }

    fun prefixedWithZeros(num: bag big_number, len: int): bag big_number {
        let newNum: bag big_number;
        createNum(len) >> eq newNum;
        let numZeros: int;
        len - num.len >> eq numZeros;
        let i: int;
        0 >> eq i;
        while (i < len) {
            if (i >= numZeros) {
                num.value[i - numZeros] >> eq newNum.value[i];
            }
            i + 1 >> eq i;
        }
        newNum >> return;
    }

    fun trimmed(num: bag big_number): bag big_number {
        let numZeros: int;
        0 >> eq numZeros;
        while (numZeros < num.len) {
            if (num.value[numZeros] != 0) {
                break;
            }
            numZeros + 1 >> eq numZeros;
        }

        let rez: bag big_number;
        createNum(num.len - numZeros) >> eq rez;
        let i: int;
        0 >> eq i;
        while (i < num.len) {
            if (i >= numZeros) {
                num.value[i] >> eq rez.value[i - numZeros];
            }

            i + 1 >> eq i;
        }

        rez >> return;
    }

    fun add(num1: bag big_number, num2: bag big_number): bag big_number {
        let addNum1: bag big_number;
        let addNum2: bag big_number;
        let rez: bag big_number;
        let len: int;
        if (num1.len < num2.len) {
            num2.len + 1 >> eq len;
        } else {
            num1.len + 1 >> eq len;
        }
        prefixedWithZeros(num1, len) >> eq addNum1;
        prefixedWithZeros(num2, len) >> eq addNum2;
        createNum(len) >> eq rez;
        let i: int;
        let excess: int;
        let sum: int;
        0 >> eq excess;
        len - 1 >> eq i;
        while (i >= 0) {
            addNum1.value[i] + addNum2.value[i] >> eq sum;

            (sum % 10) + excess >> eq rez.value[i];
            sum / 10 >> eq excess;

            i - 1 >> eq i;
        }

        trimmed(rez) >> return;
    }

    fun printNum(num: bag big_number): void {
        let i: int;
        0 >> eq i;
        while (i < num.len) {
            output num.value[i];
            i + 1 >> eq i;
        }
    }

    fun innerFib(n: int, arr: bag big_number[]): bag big_number {
      if(isPositive(arr[n])) {
        arr[n] >> return;
      }
      innerFib(n - 1, arr) >>
        add(in, innerFib(n - 2, arr)) >>
        eq arr[n];
      arr[n] >> return;
    }

    fun fibonacci(n: int): bag big_number {
      let arr: bag big_number[];
      let i: int;
      alloc bag big_number[n + 1] >> arr;
      0 >> eq i;
      while (i <= n) {
        if ((i == 0) || (i == 1)) {
          createNum(1) >> eq arr[i];
          i >> eq arr[i].value[0];
          i + 1 >> eq i;
          continue;
        }
        createNum(1) >> eq arr[i];
        -1 >> eq arr[i].value[0];
        i + 1 >> eq i;
      }
      innerFib(n, arr) >> return;
    }

    fun main(): void {
      let num: int;
      input num;
      printNum(fibonacci(num));
    }