<h2 align="center">Batch Processing in JDBC and HIBERNATE</h1>
<div align="center"><img src="https://hsto.org/webt/_h/n1/vh/_hn1vh2ptj_ez4tikrg7r_prib8.png"></div>
</br>
<p>Batching queries is one well-known performance technique that you should look out for. Reducing the number of network connections to the database and increasing the speed of query execution is a significant advantage in using batch processing.</p>
<p><b>Basic batch operations: INSERT, UPDATE, DELETE.</b></p>
<p>All the above operations are contained in interface <a href="https://github.com/yurievivan/BatchProcessing/blob/master/src/main/java/dao/batch/Dao.java">Dao</a>.</p>
<h2>Batch Processing in JDBC</h2>
<p><b>Source package: </b><a href="https://github.com/yurievivan/BatchProcessing/tree/master/src/main/java/jdbc/batch">jdbc.batch</a>.</p>
<p><b>Entity: </b><a href="https://github.com/yurievivan/BatchProcessing/blob/master/src/main/java/jdbc/batch/Book.java">Book</a>.</p>
<p><b>DAO for the Statement interface: </b><a href="https://github.com/yurievivan/BatchProcessing/blob/master/src/main/java/jdbc/batch/BookDaoStatement.java">BookDaoStatement</a>.</p>
<p><b>DAO for the PreparedStatement interface: </b><a href="https://github.com/yurievivan/BatchProcessing/blob/master/src/main/java/jdbc/batch/BookDaoPreparedStatement.java">BookDaoPreparedStatement</a>.</p>
<p><b>An example of batch processing for the CallableStatement interface: </b><a href="https://github.com/yurievivan/BatchProcessing/blob/master/src/main/java/jdbc/batch/CallStoredProcedure.java">CallStoredProcedure</a>.</p>
<h2>Batch Processing in HIBERNATE</h2>
<p><b>Source package: </b><a href="https://github.com/yurievivan/BatchProcessing/tree/master/src/main/java/hibernate/batch">hibernate.batch</a>.</p>
<p><b>Entity: </b><a href="https://github.com/yurievivan/BatchProcessing/blob/master/src/main/java/hibernate/batch/Book.java">Book</a>.</p>
<p><b>DAO: </b><a href="https://github.com/yurievivan/BatchProcessing/blob/master/src/main/java/hibernate/batch/BookDao.java">BookDao</a>.</p>
<hr>
<h2 align="left">Publishing on Habrahabr: <a href="https://habr.com/ru/post/501756/">https://habr.com/ru/post/501756/</a></h1>
