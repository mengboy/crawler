CREATE TABLE tb_zhihu(
  uid INT PRIMARY KEY NOT NULL,
  username VARCHAR(20),
  sex CHAR(2),
  address VARCHAR(20),
  business VARCHAR(20),
  job VARCHAR(100),
  edu VARCHAR(100) ,
  info text,
  url VARCHAR(20),
  imgurl VARCHAR(10)
);

CREATE TABLE tb_url(
  id int PRIMARY KEY,
  title VARCHAR(20),
  userurl VARCHAR(20)
);