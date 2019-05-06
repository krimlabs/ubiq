-- resources/sql/products.sql

-- :name insert-product :! :n
insert into products (name)
values (:name)

-- :name select-all-products
select * from products
