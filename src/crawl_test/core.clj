(ns crawl-test.core
  (:require 
    [clojure.core.async :as async]
    [clojure.java.io :as io]
    [org.httpkit.client :as http]
    [cheshire.core :as json]
    [juno.store :as store]
    [juno.store.fs :as fs-store]
    [plumbing.core :as p]
    [plumbing.graph :as graph])
  (:import
    [org.apache.pdfbox.pdmodel PDDocument]
    [org.apache.pdfbox.util PDFTextStripper])
  (:gen-class))

(def options {:timeout 1000
              :headers {"Accept" "text/json"}
              :query-params { :lang "de" :format "json"}})



(defn with-page-number [p]
  (assoc-in options [:query-params :pageNumber] p))

(def fs-bucket
  (store/bucket {:type :fs
                 :path (.getAbsolutePath (java.io.File. "store-fs-test-dir"))}))



(defn parse-response [{:keys [status headers body error]}]
  (if error
    (println "Failed, exception: " error)
    (json/parse-string body true)))

(defn has-more-pages? [r]
  (:hasMorePages (last r)))

(defn make-request!
  [url page-number]
  (http/get url (with-page-number page-number)))


(def crawl-graph
  {:response       (p/fnk [url page-number]  (make-request! url page-number))
   :result         (p/fnk [response] (parse-response @response))
   :has-more-pages (p/fnk [result] (has-more-pages? result))})

(def crawl-eager (graph/compile crawl-graph))

(defn crawl! []
  (store/put! fs-bucket "page" (crawl-eager {:url "http://ws.parlament.ch/councillors/historic" :page-number 1})))

(defn text-of-pdf
  [url]
  (with-open [pd (PDDocument/load (io/as-url url))]
    (let [stripper (PDFTextStripper.)]
    (.getText stripper pd))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [pdf-text (text-of-pdf "http://www.parlament.ch/d/organe-mitglieder/nationalrat/documents/ra-nr-interessen.pdf")]
  (store/put! fs-bucket "interests" pdf-text )))


  