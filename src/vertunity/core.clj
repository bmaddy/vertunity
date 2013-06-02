(ns vertunity.core
  (:require [net.cgrand.enlive-html :as html]
            [hiccup.page :refer [html5]]
            [hiccup.element :refer [javascript-tag]]
            [clojure.string :as s]))

; (def *base-url* "http://www.commonrootscafe.com/")

(defn fetch-url [url]
  (html/html-resource (java.net.URL. url)))

(defn local-links-from-page [page url]
  (map #(-> % :attrs :href)
       (html/select
         page
         #{[:body [:a (html/attr-contains :href url)]]})))
           ; need to make this work with relative links
           ; [:body [:a (html/attr-starts :href "/")]]})))

(defn text-html? [url]
  (let [content-type (.. (java.net.URL. url) (openConnection) (getHeaderField "Content-Type"))]
    (= (first (s/split content-type #";")) "text/html")))

(def nodes-with-text-selector (map html/but [:html :head :iframe :br :img :noscript]))

(defn get-words [node]
  (remove #(or (= "" %) (= " " %)) (s/split (s/trim (html/text node)) #"\s")))

(defn fetch-pages [pages remaining]
  (let [unfetched (ffirst (remove second pages))]
    (println (str "todo: " (keys (remove second pages))))
    (println (str "fetching (" remaining "): " unfetched))
    (if (and unfetched (pos? remaining))
      (let [page (fetch-url unfetched)
            links (filter text-html? (local-links-from-page page unfetched))]
        (println (str "adding: " (vec links)))
        (recur (merge (zipmap links (repeat nil))
                      pages
                      {unfetched page})
               (dec remaining)))
      pages)))

(defn get-words-from-page [page]
  (mapcat get-words (html/select page nodes-with-text-selector)))

(defn all-words [url]
  (mapcat get-words-from-page (vals (fetch-pages {url nil} 30))))

(defn build-page [words]
  (html5 [:head
          [:script {:src "https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"}]
          [:script {:src "jquery.awesomeCloud-0.2.min.js"}]]
         [:body
          [:div {:id "wordcloud1" :style "border:1px solid #f00;height:500px;width:500px;"}
           (for [w words]
             [:span {:data-weight (second w)} (first w)])]
          (javascript-tag "$(document).ready(function(){
                             $('#wordcloud1').awesomeCloud({
                               'size' : {
                                 'grid' : 16,
                                 'normalize' : false
                               },
                               'options' : {
                                 'color' : 'random-dark',
                                 'rotationRatio' : 0.2,
                                 'printMultiplier' : 3,
                                 'sort' : 'highest'
                               },
                               'font' : '\\'Times New Roman\\', Times, serif',
                               'shape' : 'square'
                             })
                           });")]))

(comment
  (def words (all-words "http://www.mn350.org/"))
  (spit "mn350.org.html" (build-page (take 100 (reverse (sort-by second (frequencies words))))))
  )
