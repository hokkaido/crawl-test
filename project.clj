(defproject crawl-test "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [http-kit "2.1.16"]
                 [cheshire "5.3.1"]
                 [clj-http "0.9.2"]
                 [prismatic/plumbing "0.3.1"]
                 [juno.store "0.1.1-SNAPSHOT"]
                 [org.apache.pdfbox/pdfbox "1.8.5"]]
  :main ^:skip-aot crawl-test.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
