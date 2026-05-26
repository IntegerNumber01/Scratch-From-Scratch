# Scratch Language User Manual

This document is a user manual for the Scratch-style language used in this project. It stays close to the existing official PDF, while matching the current parser and interpreter in the codebase.

## 1. Basic File Layout

This project uses two kinds of `.scratch` files:

- `backdrop.scratch`
  - Holds global variable assignments only.
- `script.scratch`
  - Lives inside each sprite folder.
  - Holds event scripts, custom functions, variable assignments, and commands for that sprite.

Example project layout:

```text
SBGame/
|-- backdrop.scratch
|-- Player/
|   |-- script.scratch
|   `-- Player.png
`-- Enemy/
    |-- script.scratch
    `-- Enemy.png
```

## 2. Writing Rules

- Use 4 spaces for each indentation level.
- Top-level event blocks and function definitions should not be indented.
- Commands inside a block should be indented one level.
- Use `else:` on its own line, aligned with its matching `if`.
- Comments start with `#` and continue to the end of the line.

Example:

```scratch
when_flag_clicked:
    score = 0
    if(score == 0):
        say(Start)
    else:
        say(Running)
```

## 3. Events

These are the top-level script starters:

- `when_flag_clicked:`
  - Runs when the green flag is clicked.
- `when_i_start_as_clone:`
  - Runs when a clone of the sprite is created.

Example:

```scratch
when_flag_clicked:
    go_to(0, 0)
    create_clone()

when_i_start_as_clone:
    go_to_random_position()
```

## 4. Variables

### Global variables

Global variables must be created in `backdrop.scratch`.

Example:

```scratch
Score = 0
Lives = 3
```

`backdrop.scratch` only supports assignments. Do not place events, functions, or other commands there.

### Sprite/local variables

Inside a sprite `script.scratch`, create or update variables with `=`.

Example:

```scratch
speed = 5
speed = speed + 1
```

### Built-in state variables

These names are already used by the engine:

- `x_position`
- `y_position`
- `direction`
- `size`
- `mouse_x`
- `mouse_y`
- `mouse_down`

Do not reuse those names for your own variables.

Note: the current parser does not support a `global` keyword. To make a variable global, define it in `backdrop.scratch`.

## 5. Custom Functions

Create functions with `define`.

Syntax:

```scratch
define my_method(arg1, arg2):
    say(arg1)
```

Call the function like a normal command:

```scratch
my_method(Hello, 10)
```

Notes:

- Function arguments are written as plain names only.
- Do not include types like `String` or `int`.
- Avoid using reserved engine names for function arguments.

## 6. Math Operators

These operators are supported inside expressions:

- `+` add
- `-` subtract
- `*` multiply
- `/` divide

Example:

```scratch
speed = 10 + 5 * 2
distance = (speed + 4) / 2
```

## 7. Comparison Operators

These comparisons are supported:

- `>`
- `<`
- `==`

Example:

```scratch
if(score > 10):
    say(Win)
```

If you need a "not equal" style check, use `not (...)` around an equality test:

```scratch
if(not (score == 10)):
    say(NotTen)
```

## 8. Boolean Operators

These boolean operators are supported:

- `and`
- `or`
- `not`

Example:

```scratch
if(key_pressed(left) or key_pressed(a)):
    change_x(-5)
```

## 9. Block Commands

### `if(condition):`

Runs the indented block if the condition is true.

```scratch
if(mouse_down):
    say(Clicked)
```

### `else:`

Runs when the matching `if` condition is false.

```scratch
if(score > 0):
    say(Positive)
else:
    say(Zero)
```

### `repeat(times):`

Repeats the block a fixed number of times.

```scratch
repeat(10):
    move(5)
```

### `repeat_until(condition):`

Repeats the block until the condition becomes true.

```scratch
repeat_until(x_position == 240):
    move(5)
```

### `forever():`

Repeats the block continuously while the program is running.

```scratch
forever():
    turn_right(5)
```

## 10. Action Commands

### Movement and direction

- `move(steps)`
- `go_to(x, y)`
- `turn_right(degrees)`
- `turn_left(degrees)`
- `point_in_direction(degrees)`
- `change_x(amount)`
- `change_y(amount)`
- `go_to_random_position()`
- `set_x(x)`
- `set_y(y)`

### Looks and costumes

- `switch_costume(filename)`
- `next_costume()`
- `say(text)`
- `say_for_time(text, seconds)`
- `think(text)`
- `think_for_time(text, seconds)`
- `set_size(size)`
- `change_size(amount)`
- `hide()`
- `show()`

### Cloning and variable monitors

- `create_clone()`
- `show_variable(name)`
- `hide_variable(name)`

### Layering

- `go_to_front_layer()`
- `go_to_back_layer()`
- `go_forward_layers(amount)`
- `go_backward_layers(amount)`

Examples:

```scratch
switch_costume(Hammer.png)
say_for_time(ouch!, 1)
show_variable(Score)
go_to_front_layer()
```

Notes:

- `switch_costume(...)` expects a raw costume filename such as `Hammer.png`.
- `show_variable(...)` and `hide_variable(...)` expect the variable name directly.

## 11. Function Operators

These can be used inside expressions.

### Input and sprite state

- `key_pressed(key)`
- `touching(sprite_name)`
- `touching(mouse_pointer)`
- `attribute_of_sprite(attribute, sprite_name)`

Examples:

```scratch
if(key_pressed(space)):
    change_y(10)

if(touching(Mole)):
    say(Hit)

other_x = attribute_of_sprite(x_position, Mole)
```

Supported keyboard names currently include:

- letters `a` through `z`
- digits `0` through `9`
- `left`
- `right`
- `up`
- `down`
- `space`
- `enter`
- `shift`

### Utility operators

- `pick_random(x, y)`
- `join(str1, str2)`
- `letter_of(index, text)`
- `length_of(text)`
- `round(value)`
- `mod(a, b)`

### Math functions

- `abs(value)`
- `floor(value)`
- `ceiling(value)`
- `sqrt(value)`
- `sin(value)`
- `cos(value)`
- `tan(value)`
- `asin(value)`
- `acos(value)`
- `atan(value)`
- `ln(value)`
- `log(value)`

Examples:

```scratch
rand_x = pick_random(-100, 100)
name_len = length_of(Player)
rounded = round(4.6)
remainder = mod(10, 3)
wave = sin(90)
```

## 12. Example Program

This is the PDF example rewritten in the current project style:

```scratch
when_flag_clicked:
    go_to(0, 0)
    point_in_direction(90)
    count = 0
    repeat_until(x_position == 240):
        move(5)
        count = count + 1
        say(count)
    say(AllDone!)
```

## 13. Practical Notes

- Use colons after event headers, function definitions, and block commands.
- Text arguments work best as single tokens without spaces.
- Global variables are shared across sprites through `backdrop.scratch`.
- Built-in sprite state values such as `x_position` and `direction` update automatically.
- Clones run their `when_i_start_as_clone:` script when created.
- Many motion and size commands evaluate numbers and then use whole-number values at runtime.
- The older PDF mentions `e^(...)` and `10^(...)`, but the current parser does not reliably accept those forms.

## 14. Quick Reference

### Events

- `when_flag_clicked:`
- `when_i_start_as_clone:`

### Blocks

- `if(...)`
- `else:`
- `repeat(...)`
- `repeat_until(...)`
- `forever()`

### Variables

- Assignment: `name = expression`
- Globals: define in `backdrop.scratch`

### Main built-in values

- `x_position`
- `y_position`
- `direction`
- `size`
- `mouse_x`
- `mouse_y`
- `mouse_down`
