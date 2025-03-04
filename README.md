
# PavaLang Interpreter
![PavaLang Poster](https://i.ibb.co/9kK6V80X/pavalang-poster.png)
  

  

This document provides comprehensive documentation for the Pava language. Pava is a dynamically typed, interpreted language with a syntax inspired by modern scripting languages. It supports variables, user-defined functions (with and without default arguments), control flow statements, arrays (lists), modules, built-in (native) functions, and more. This documentation covers syntax tutorials, built-in functions with examples, array operations, module usage, and complete code examples.

  

---

  

## Table of Contents

  

-  [Introduction](#introduction)

-  [Lexical Structure](#lexical-structure)

-  [Syntax Overview](#syntax-overview)

-  [Variables](#variables)

-  [Functions](#functions)

-  [User-Defined Functions](#user-defined-functions)

-  [Default Function Arguments](#default-function-arguments)

-  [Return Statements](#return-statements)

-  [Control Flow](#control-flow)

-  [If Statements](#if-statements)

-  [While Statements](#while-statements)

-  [For Statements](#for-statements)

-  [Logical Operators](#logical-operators)

-  [Blocks and Scoping](#blocks-and-scoping)

-  [Arrays (Lists)](#arrays-lists)

-  [Array Literals](#array-literals)

-  [Index Access and Assignment](#index-access-and-assignment)

-  [Fixed-Size Array Declarations](#fixed-size-array-declarations)

-  [Modules](#modules)

-  [Built-In Functions](#built-in-functions)

-  [Code Examples](#code-examples)

-  [Error Handling](#error-handling)

-  [Conclusion](#conclusion)

  

---

  

## Introduction

  

Pavalang is a lightweight tree-walking interpreter designed to teach language implementation techniques. It supports dynamic typing, first-class functions, closures, and a set of built-in functionalities that allow you to build applications quickly. 
Pava is written entirely in Java and is syntatically similar to Python hence, Pava.
This documentation covers the complete syntax and built-in functionalities of Pava.

  

---

  

## Lexical Structure

  

### Comments

Use `//` for single-line comments.

  

### Tokens

Pava tokens include:

- Parentheses `(`, `)`

- Braces `{`, `}`

- Brackets `[`, `]`

- Commas and semicolons

- Operators (`+`, `-`, `*`, `/`, etc.)

- Identifiers

- Strings (enclosed in double quotes)

- Numbers

- Keywords (`let`, `fun`, `if`, `while`, `for`, `return`, `pao`, `kaddo`, etc.)

  

### Shebang

A file starting with `#!` (e.g., `#!/bin/pava`) is ignored by the lexer.

  

---

  

## Syntax Overview

  

### Variables

  

#### Declaration

Variables are declared using the `let` keyword.

  

**Example:**

```Pava

let a = 10;

let name = "Alice";

let flag = true;

```

  

#### Assignment

Variables can be reassigned using the `=` operator.

  

**Example:**

```Pava

a = 20;

```

### Print statement with Escape Sequences

```Pava

print "a\nb\tc \\n";

```

Outputs:

```Console

a
b    c \n

```
  

### Functions

  

#### User-Defined Functions

  

**Syntax:**

Functions are declared with the `fun` keyword, followed by the function name, a parameter list in parentheses, and a block for the body.

  

**Example without Arguments:**

```Pava

fun greet() {

print "Hello!";

}

greet();

```

  

**Example with Arguments:**

```Pava

fun add(x, y) {

return x + y;

}

print add(3, 5); // Output: 8

```

  

#### Default Function Arguments

  

**Syntax:**

Parameters can have default values. Default parameters must be the rightmost parameters.

  

**Valid Example:**

```Pava

fun foo(x, y = 20) {

print x + y;

}

foo(5); // Uses default: Output: 25

foo(5, 30); // Output: 35

```

  

**Invalid Example:**

```Pava

fun bar(x = 10, y) {} // Error: Parameters with default values must be trailing.

```

  

#### Return Statements

  

**Syntax:**

Use the `return` keyword to return a value from a function.

  

**Examples:**

```Pava

fun foo() { return 10; }

print foo(); // Output: 10

  

fun f() {

if (false) return "no"; else return "ok";

}

print f(); // Output: ok

  

fun f() {

return;

print "bad"; // Unreachable code.

}

print f(); // Output: nil

```

  

### Control Flow

  

#### If Statements

The `if` statement tests a condition, executing the "then" branch if true; an optional `else` branch is available.

  

**Example:**

```Pava

if (true) print "good";

if (true) {

print "block body";

}

```

  

#### While Statements

The `while` statement repeatedly executes its body while the condition remains truth *( No, Pava doesn't support ++)*.

  

**Example:**

```Pava

let i = 0;

while (i < 3) {

print i;

i = i + 1;

}

// Output: 0, 1, 2 (each on a new line)

```

  

#### For Statements

For statements are desugared into a block that includes an initializer, condition, and increment clause. `For` is just syntatic sugar with underlying while loop.

  

**Examples:**

```Pava

// For with missing increment:

for (let baz = 0; baz < 3;) print baz = baz + 1;

// Output: 1, 2, 3

  

// Full for statement:

for (let world = 0; world < 3; world = world + 1) {

print world;

}

// Output: 0, 1, 2

```

  

#### Logical Operators

  

**Operators:**

- Logical AND (`and`) returns the first falsy value (or the last value if all are truthy).

- Logical OR (`or`) returns the first truthy value.

  

**Examples:**

```Pava

print false and "ok"; // Output: false

  

if ((true and "hello")) print "hello"; // Output: hello

if ((97 and "baz")) print "baz"; // Output: baz

  

if ((false or "baz")) print "baz"; // Output: baz

if ((true or "world")) print "world"; // Output: true (short-circuits)

```

  

### Blocks and Scoping

  

Blocks are enclosed in `{ }` and group statements. They also create a new local scope.

  

**Examples:**

```Pava

{

let foo = "before";

print foo;

}

{

let foo = "after";

print foo;

}

// Output: before, then after

  

{

let hello = 88;

{

let foo = 88;

print foo;

}

print hello;

}

// Output: 88, then 88

```

  

### Arrays (Lists)

  

#### Array Literals

An array is a comma-separated list of values enclosed in square brackets.

  

**Examples:**

```Pava

let arr = [1,2,3,4,5];

print arr[0]; // Output: 1

print arr[3]; // Output: 4

print length(arr); // Output: 5

  

let li = ["abc", 123.23, true];

print li[0]; // Output: abc

print li[1]; // Output: 123.23

print li[2]; // Output: true

```

  

#### Index Access & Assignment

Use square brackets to access and assign to array elements.

  

**Examples:**

```Pava

let li = [1,2,3,4,5];

print li[0]; // Output: 1

li[0] = 69;

print li[0]; // Output: 69

  

let str = "ABCDEF";

print str; // Output: ABCDEF

str[0] = "Z";

str[1] = "Y";

print str; // Output: ZYCDEF

```

  

#### Fixed-Size Array Declarations

Declaring a variable with square brackets after its name creates an array of fixed size with all elements initialized to `nil`.

  

**Example:**

```Pava

let arr[3];

arr[0] = 71;

arr[1] = 29;

arr[2] = 12;

print arr[0]; // Output: 71

print arr[1]; // Output: 29

print arr[2]; // Output: 12

```

  

#### Bounds Checking

Accessing an index out of range results in a runtime error.

  

### Modules

  

Modules are separate Pava files that can contain function definitions and variables. A module file ends with an export statement, and modules are imported using the `pao` keyword (Pun intended).

  

**Module File Example (math.Pava):**

```Pava

fun addTwo(x, y) {

return x + y;

}

  

fun multiplyTwo(x, y) {

return x * y;

}

  

kaddo Math;

```

  

**Importing a Module (main.Pava):**

```Pava

pao "math"; // Extension (.pava) is optional; system will search /usr/local/share/pava first.

  

print Math.addTwo(3,5); // Output: 8

print Math.multiplyTwo(2,7); // Output: 14

```

  

**Module Import Behavior:**

1. The interpreter first searches for the module in `/usr/local/share/pava`

2. If not found, it searches in the working directory

3. The `pao` statement ignores the extension if not provided

4. The module file must end with a `kaddo` statement

5. When imported, the module's exported namespace is accessible via dot notation

  
  

## Built-In Functions

  

Pava provides a set of built-in (native) functions that are bound in the global environment and can be called like any other function.

  

### `clock()`

**Description:** Returns the current time in milliseconds since midnight January 1, 1970 UTC (UNIX Timestamp).

  

**Usage:**

```Pava

print clock();

print clock() + 75;

```

  

**Example Output:**

```

1731411035

1731411110

```

  

### `pava()`

**Description:** Returns the string "Akhtar Lava" :)

  

**Usage:**

```Pava

print pava();

```

  

**Example Output:**

```

Akhtar Lava

```

  

### `typeof()`

**Description:** Returns a string representing the type of its argument. Possible types include: NUMBER, STRING, BOOLEAN, FUNCTION, NIL, OBJECT.

  

**Usage:**

```Pava

print typeof(123); // Output: NUMBER

let x = "Hello, World!";

print typeof(x); // Output: STRING

```

  

### `input()`

**Description:** Prompts the user for input. If a prompt string is provided, it is displayed; otherwise, no prompt is shown. Returns the input as a string.

  

**Usage:**

```Pava

let x = input("Enter a number: ");

print "The number is: " + x;

let age = input();

print "Your age is: " + age;

```

  

**Example Output:**

```

Enter a number: 98.43

The number is: 98.43

18

Your age is: 18

```

  

### `err()`

**Description:** Prints the provided message to the standard error stream.

  

**Usage:**

```Pava

err("Fatal error!");

```

  

**Example Output (stderr):**

```

Fatal error!

```

  

### `string(Number)`

**Description:** Converts a number to its string representation.

  

**Usage:**

```Pava

print string(123);

```

  

**Example Output:**

```

123

```

  

### `number(String)`

**Description:** Converts a string to a number.

  

**Usage:**

```Pava

print number("123");

```

  

**Example Output:**

```

123

```

  

### `shell(command)`

**Description:** Executes the provided shell command and returns its output as a string.

  

**Usage:**

```Pava

print shell("echo hello");

```

  

**Example Output:**

```

hello

```

  

### `length(String/Array)`

**Description:** Returns the length of a string or array.

  

**Usage:**

```Pava

print length("abc"); // Output: 3

let arr = [1,2,3,4,5];

print length(arr); // Output: 5

```

  

### `exit(Number)`

**Description:** Exits the program with the given exit code *(Exit Code must be positive)* .

  

**Usage:**

```Pava

exit(0);

```

  

**Effect:** Terminates the interpreter with the provided exit code.

  

### `getAsciiCode(character)`

**Description:** Returns the ASCII code of a single-character string *(No, Pava doesn't support single quotes for characters)*.

  

**Usage:**

```Pava

print getAsciiCode("A"); // Output: 65

```

  

### `fromAsciiCode(Number)`

**Description:** Converts a number (ASCII code) to its corresponding character as a string.

  

**Usage:**

```Pava

print fromAsciiCode(65); // Output: A

```

  

## Error Handling

  

Pava provides runtime and syntax error messages with line numbers.

  

### Syntax Errors

When a syntax error is encountered, a message is displayed in the format:

  

```

[line X] Syntax Error: <message>

```

  

### Runtime Errors

For example, trying to access an out-of-range index or using an invalid operand will print:

  

```

[line X] Runtime Error: <message>

```

  

### Return Statement

If a function returns a value using `return`, it will properly exit the function body.

  

### Shebang

Files starting with `#!` are automatically skipped by the lexer.

  

## Conclusion

  

Pava is a full-featured scripting language designed for learning language implementation techniques. This documentation has covered:

  

1.  **Lexical and Syntactic Structure:** Variables, functions (with and without default arguments), control flow (if, while, for), logical operators, blocks, arrays/lists, modules, and more.

  

2.  **Built-In Functions:** A wide array of native functions for time, string/number conversion, shell commands, error printing, and ASCII manipulation.

  

3.  **Modules:** How to import/export modules and access module members.

  

4.  **Arrays/Lists:** Array literals, indexing, bounds checking, index assignment, and fixed-size array declarations.

  

5.  **Error Handling:** Both syntax and runtime errors are reported with line numbers.

  

6.  **Examples:** Comprehensive code examples demonstrate each language feature.

  

This documentation serves as a complete reference for developers using Pava. As Pava evolves, additional features (such as classes, more built-in functions, and improved module systems) could be added. For now, you have a robust language implementation that supports a rich set of features ideal for learning and rapid prototyping.

  

**Happy coding in Pava!**