-- 以下脚本在MySQL8.0下进行执行
-- 创建数据库
-- CREATE DATABASE db_name;
CREATE DATABASE shop;

-- 当前使用数据库shop
USE shop;

-- 创建表
-- CREATE TABLE tb_name (field_1 data_type field_constraints_1 ..., table_constraints_1);
CREATE TABLE Product (
    product_id CHAR(4) NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    product_type VARCHAR(32) NOT NULL,
    sale_price INTEGER,
    purchase_price INTEGER,
    regist_date DATE,
    PRIMARY KEY (product_id)
);

-- 表操作
-- DROP TABLE tb_name;
-- ALTER TABLE tb_name ADD COLUMN <field define>;
-- ALTER TABLE tb_name DROP COLUMN <field_name>;

START TRANSACTION;
INSERT INTO Product VALUES ('0001', 'T恤衫', '衣服', 1000, 500, '2009-09-20');
INSERT INTO Product VALUES ('0002', '打孔器', '办公用品', 500, 320, '2009-09-11');
INSERT INTO Product VALUES ('0003', '运动T恤', '衣服', 4000, 2800, NULL);
INSERT INTO Product VALUES ('0004', '菜刀', '厨房用具', 3000, 2800, '2009-09-20');
INSERT INTO Product VALUES ('0005', '高压锅', '厨房用具', 6800, 500, '2009-01-15');
INSERT INTO Product VALUES ('0006', '叉子', '厨房用具', 500, NULL, '2009-09-20');
INSERT INTO Product VALUES ('0007', '擦菜板', '厨房用具', 800, 790, '2008-04-28');
INSERT INTO Product VALUES ('0008', '圆珠笔', '办公用品', 100, NULL, '2009-11-11');
COMMIT;


-- Chap1练习题

CREATE TABLE Addressbook (
    regist_no INTEGER NOT NULL,
    name VARCHAR(128) NOT NULL,
    address VARCHAR(256) NOT NULL,
    tel_no CHAR(10),
    mail_address CHAR(20),
    PRIMARY KEY (regist_no)
);

ALTER TABLE  Addressbook ADD postal_code CHAR(8) NOT NULL;

RENAME TABLE Addressbook TO DelAddressbook;
RENAME TABLE DelAddressbook TO Addressbook;