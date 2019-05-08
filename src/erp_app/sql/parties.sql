-- products/sql/parties.sql

-- :name insert-party :! :n
insert into parties (name, address)
values (:name, :address)

-- :name select-all-parties
select * from parties
