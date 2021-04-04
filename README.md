This is graphical implementation of Dining Philosophers problem using actor model.
For more info about dining philosophers problem you can see below link:

[Dining Philosophers Problem](https://en.wikipedia.org/wiki/Dining_philosophers_problem)

And for more info about actor model you can see below link:

[Actor Model](https://medium.com/@KtheAgent/actor-model-in-nutshell-d13c0f81c8c7#:~:text=Actor%20Model%20is%20a%20conceptual%20concurrent%20computation%20model%2C%20came%20into,Actor%20Model%20are%20Akka%20%26%20Erlang.)

Each blue circle is a philosopher.
Each circle with 'R' inside denote right hand of philosopher and each circle with 'L' inside denote left hand of philosopher.
Each circle with number on table and between philosophers are forks that can be empty or green circle.
The green color of fork circles or 'R' or 'L' circles denote that fork is there.
For example if a 'R' circle of philosopher x be green, that means philosopher x get right side fork.
To implement this problem i'm using akka library in java. for more info you can see below link:

[Akka](https://akka.io/)

![dining-philosophers](https://user-images.githubusercontent.com/69300875/113518803-ca37f100-959d-11eb-9d81-f86277829012.gif)


