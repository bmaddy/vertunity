# vertunity

A Clojure library designed to ... well, that part is up to you.

## Usage

Install leiningen

Run this to make stuff happen:
    lein repl
    (use 'vertunity.core)
    (def words (all-words "http://doitgreen.org/"))
    (spit "doitgreen.org.html" (build-page (take 100 (reverse (sort-by second (frequencies words))))))

There are some example outputs in the root directory.
If you can't see any words after creating a page, you probably have to play with the 'printMultiplier' value in the javascript. It seems to happen when the weights get really big on large sites.

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
