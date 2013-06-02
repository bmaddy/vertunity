# vertunity

A Clojure library designed to ... well, that part is up to you.

## Usage

Install leiningen

Run this to make stuff happen:
    lein repl
    (use 'vertunity.core)
    (def words (all-words "http://doitgreen.org/"))
    (spit "doitgreen.org.html" (build-page (take 100 (reverse (sort-by second (frequencies words))))))

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.
