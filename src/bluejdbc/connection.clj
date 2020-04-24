(ns bluejdbc.connection
  (:require [bluejdbc.driver :as driver]
            [bluejdbc.options :as options]
            [bluejdbc.result-set :as rs]
            [bluejdbc.statement :as stmt]
            [bluejdbc.util :as u]
            [clojure.tools.logging :as log]
            [methodical.core :as m]
            [potemkin.types :as p.types]
            [pretty.core :as pretty])
  (:import [java.sql Connection DriverManager]
           javax.sql.DataSource))

(u/define-enums transaction-isolation-level Connection #"^TRANSACTION_")

(p.types/deftype+ ProxyConnection [^Connection conn mta opts]
  pretty/PrettyPrintable
  (pretty [_]
    (list 'proxy-connection conn opts))

  options/Options
  (options [_]
    opts)

  (with-options [_ new-options]
    (ProxyConnection. conn mta new-options))

  clojure.lang.IObj
  (meta [_]
    mta)

  (withMeta [_ new-meta]
    (ProxyConnection. conn new-meta opts))

  java.sql.Wrapper
  (^boolean isWrapperFor [this ^Class interface]
   (or (instance? interface this)
       (.isWrapperFor conn interface)))

  (unwrap [this ^Class interface]
    (if (instance? interface this)
      this
      (.unwrap conn interface)))

  Connection
  (^void abort [_ ^java.util.concurrent.Executor a] (.abort conn a))
  (^void beginRequest [_] (.beginRequest conn))
  (^void clearWarnings [_] (.clearWarnings conn))
  (^void close [_] (.close conn))
  (^void commit [_] (.commit conn))
  (^java.sql.Array createArrayOf [_ ^String a ^ "[Ljava.lang.Object;" b] (.createArrayOf conn a b))
  (^java.sql.Blob createBlob [_] (.createBlob conn))
  (^java.sql.Clob createClob [_] (.createClob conn))
  (^java.sql.NClob createNClob [_] (.createNClob conn))
  (^java.sql.SQLXML createSQLXML [_] (.createSQLXML conn))
  (^java.sql.Statement createStatement [_] (.createStatement conn))
  (^java.sql.Statement createStatement [_ ^int a ^int b ^int c] (.createStatement conn a b c))
  (^java.sql.Statement createStatement [_ ^int a ^int b] (.createStatement conn a b))
  (^java.sql.Struct createStruct [_ ^String a ^ "[Ljava.lang.Object;" b] (.createStruct conn a b))
  (^void endRequest [_] (.endRequest conn))
  (^boolean getAutoCommit [_] (.getAutoCommit conn))
  (^String getCatalog [_] (.getCatalog conn))
  (^java.util.Properties getClientInfo [_] (.getClientInfo conn))
  (^String getClientInfo [_ ^String a] (.getClientInfo conn a))
  (^int getHoldability [_] (.getHoldability conn))
  (^java.sql.DatabaseMetaData getMetaData [_] (.getMetaData conn))
  (^int getNetworkTimeout [_] (.getNetworkTimeout conn))
  (^String getSchema [_] (.getSchema conn))
  (^int getTransactionIsolation [_] (.getTransactionIsolation conn))
  (^java.util.Map getTypeMap [_] (.getTypeMap conn))
  (^java.sql.SQLWarning getWarnings [_] (.getWarnings conn))
  (^boolean isClosed [_] (.isClosed conn))
  (^boolean isReadOnly [_] (.isReadOnly conn))
  (^boolean isValid [_ ^int a] (.isValid conn a))
  (^String nativeSQL [_ ^String a] (.nativeSQL conn a))
  (^java.sql.CallableStatement prepareCall [_ ^String a] (.prepareCall conn a))
  (^java.sql.CallableStatement prepareCall [_ ^String a ^int b ^int c] (.prepareCall conn a b c))
  (^java.sql.CallableStatement prepareCall [_ ^String a ^int b ^int c ^int d] (.prepareCall conn a b c d))

  (^java.sql.PreparedStatement prepareStatement
   [this ^String sql]
   (stmt/proxy-prepared-statement (.prepareStatement conn sql) (assoc opts :_connection this)))

  (^java.sql.PreparedStatement prepareStatement
   [this ^String sql ^"[Ljava.lang.String;" column-names]
   (-> (.prepareStatement conn sql column-names)
       (stmt/proxy-prepared-statement (assoc opts :_connection this))))

  (^java.sql.PreparedStatement prepareStatement
   [this ^String sql ^ints column-indexes]
   (-> (.prepareStatement conn sql column-indexes)
       (stmt/proxy-prepared-statement (assoc opts :_connection this))))

  (^java.sql.PreparedStatement prepareStatement
   [this ^String sql ^int auto-generated-keys]
   (-> (.prepareStatement conn sql auto-generated-keys)
       (stmt/proxy-prepared-statement (assoc opts :_connection this))))

  (^java.sql.PreparedStatement prepareStatement
   [this ^String sql ^int result-set-type ^int result-set-concurrency]
   (-> (.prepareStatement conn sql result-set-type result-set-concurrency)
       (stmt/proxy-prepared-statement (assoc opts
                                             :_connection this
                                             :result-set/type (u/reverse-lookup rs/type result-set-type)
                                             :result-set/concurrency (u/reverse-lookup rs/concurrency result-set-concurrency)))))

  (^java.sql.PreparedStatement prepareStatement
   [this ^String sql ^int result-set-type ^int result-set-concurrency ^int result-set-holdability]
   (-> (.prepareStatement conn sql result-set-type result-set-concurrency result-set-holdability)
       (stmt/proxy-prepared-statement (assoc opts
                                             :_connection this
                                             :result-set/type (u/reverse-lookup rs/type result-set-type)
                                             :result-set/concurrency (u/reverse-lookup rs/concurrency result-set-concurrency)
                                             :result-set/holdability (u/reverse-lookup rs/holdability result-set-holdability)))))

  (^void releaseSavepoint [_ ^java.sql.Savepoint a] (.releaseSavepoint conn a))
  (^void rollback [_] (.rollback conn))
  (^void rollback [_ ^java.sql.Savepoint a] (.rollback conn a))
  (^void setAutoCommit [_ ^boolean a] (.setAutoCommit conn a))
  (^void setCatalog [_ ^String a] (.setCatalog conn a))
  (^void setClientInfo [_ ^java.util.Properties a] (.setClientInfo conn a))
  (^void setClientInfo [_ ^String a ^String b] (.setClientInfo conn a b))
  (^void setHoldability [_ ^int a] (.setHoldability conn a))
  (^void setNetworkTimeout [_ ^java.util.concurrent.Executor a ^int b] (.setNetworkTimeout conn a b))
  (^void setReadOnly [_ ^boolean a] (.setReadOnly conn a))
  (^java.sql.Savepoint setSavepoint [_] (.setSavepoint conn))
  (^java.sql.Savepoint setSavepoint [_ ^String a] (.setSavepoint conn a))
  (^void setSchema [_ ^String a] (.setSchema conn a))
  (^void setShardingKey [_ ^java.sql.ShardingKey a] (.setShardingKey conn a))
  (^void setShardingKey [_ ^java.sql.ShardingKey a ^java.sql.ShardingKey b] (.setShardingKey conn a b))
  (^boolean setShardingKeyIfValid [_ ^java.sql.ShardingKey a ^java.sql.ShardingKey b ^int c] (.setShardingKeyIfValid conn a b c))
  (^boolean setShardingKeyIfValid [_ ^java.sql.ShardingKey a ^int b] (.setShardingKeyIfValid conn a b))
  (^void setTransactionIsolation [_ ^int a] (.setTransactionIsolation conn a))
  (^void setTypeMap [_ ^java.util.Map a] (.setTypeMap conn a)))

(defn proxy-connection
  "Wrap a `Connection` in a `ProxyConnection`, if not already wrapped."
  (^ProxyConnection [conn]
   (proxy-connection conn nil))

  (^ProxyConnection [conn options]
   (when conn
     (if (instance? ProxyConnection conn)
       (options/with-applied-options conn options)
       (do
         (options/set-options! conn options)
         (ProxyConnection. conn nil options))))))

(p.types/defprotocol+ CreateConnection
  "Protocol for anything that can used to create a new `Connection`."
  (connect!* ^bluejdbc.connection.ProxyConnection [this options]
    "Create a new JDBC connection from `this`."))

(defn create-connection-from-url!
  "Create a new JDBC connection from a JDBC URL."
  ^ProxyConnection [^String s {driver           :connection/driver
                               ^String user     :connection/user
                               ^String password :connection/password
                               properties       :connection/properties
                               :as              options}]
  (log/trace "Creating new Connection from JDBC connection string")
  (let [conn    (cond
                  driver             (.connect (driver/driver driver) s (options/->Properties properties))
                  (or user password) (DriverManager/getConnection s user password)
                  properties         (DriverManager/getConnection s (options/->Properties properties))
                  :else              (DriverManager/getConnection s))
        options (assoc options :connection/type (keyword (second (re-find #"^jdbc:([^:]+):" s))))]
    (proxy-connection conn options)))

(defn create-connection-from-clojure-java-jdbc-map!
  "Create a new JDBC connection from a `clojure.java.jdbc`-style map."
  ^ProxyConnection [m options]
  (log/trace "Getting new connection with legacy `clojure.java.jdbc` details map")
  (cond
    ;; TODO -- this should probably throw an Exception because it's going to screw up lifecycles
    (:connection m)
    (connect!* (:connection m) options)

    (:datasource m)
    (connect!* (:datasource m) options)

    :else
    ;; TODO -- other stuff
    (throw (ex-info "TODO" {}))))

(defn create-connection-from-datasource!
  "Create a new JDBC connection from a DataSource."
  ^ProxyConnection [^javax.sql.DataSource data-source {^String user :connection/user, ^String password :connection/password, :as options}]
  (log/trace "Getting new Connection from DataSource")
  (let [conn (if (or user password)
               (.getConnection data-source user password)
               (.getConnection data-source))]
    (proxy-connection conn options)))

(extend-protocol CreateConnection
  ;; JDBC connection URL
  String
  (connect!* [s options]
    (create-connection-from-url! s options))

  javax.sql.DataSource
  (connect!* [data-source options]
    (create-connection-from-datasource! data-source options))

  ;; legacy `clojure.java.jdbc`-style map
  clojure.lang.IPersistentMap
  (connect!* [m options]
    (create-connection-from-clojure-java-jdbc-map! m options)))

(defn connect!
  "Create a new JDBC connection from `source`."
  (^ProxyConnection [connectable]
   (connect!* connectable nil))

  (^ProxyConnection [connectable options]
   (connect!* connectable options)))

(defn do-with-connection
  "Impl for `with-connection`."
  [connectable options f]
  (if (instance? Connection connectable)
    (let [conn (proxy-connection connectable options)]
      (f conn))
    (with-open [conn (connect! connectable options)]
      (f conn))))

(defmacro with-connection
  "Execute `body` with `conn-binding` bound to a `Connection`. If `connectable` is already a `Connection`, `body` is executed
  using that `Connection`; if `connectable` is something else like a JDBC URL, a new `Connection` will be created for the
  duration of `body` and closed afterward.

  You can use this macro to accept either a `Connection` or something that can be used to create a `Connection` and
  handle either case appropriately."
  {:arglists '([[conn-binding connectable] & body] [[conn-binding connectable options] & body])}
  [[conn-binding connectable options] & body]
  `(do-with-connection ~connectable ~options (fn [~(vary-meta conn-binding assoc :tag 'bluejdbc.connection.ProxyConnection)]
                                               ~@body)))


;;;; Option handling

(m/defmethod options/set-option! [Connection :connection/auto-commit?]
  [^Connection conn _ auto-commit?]
  (.setReadOnly conn (boolean auto-commit?)))

(m/defmethod options/set-option! [Connection :connection/catalog]
  [^Connection conn _ catalog]
  (doto conn
    (.setCatalog (str catalog))))

(m/defmethod options/set-option! [Connection :connection/client-info]
  [^Connection conn _ properties]
  (doto conn
    (.setClientInfo conn (options/->Properties properties))))

(m/defmethod options/set-option! [Connection :result-set/holdability]
  [^Connection conn _ holdability]
  (doto conn
    (.setHoldability (rs/holdability holdability))))

(m/defmethod options/set-option! [Connection :connection/read-only?]
  [^Connection conn _ read-only?]
  (doto conn
    (.setReadOnly (boolean read-only?))))

(m/defmethod options/set-option! [Connection :connection/schema]
  [^Connection conn _ schema]
  (doto conn
    (.setSchema (str schema))))

(m/defmethod options/set-option! [Connection :connection/transaction-isolation]
  [^Connection conn _ level]
  (doto conn
    (.setTransactionIsolation (transaction-isolation-level level))))
