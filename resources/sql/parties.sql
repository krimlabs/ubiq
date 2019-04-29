-- src/sql/parties.sql

-- :name insert-party :! :n
insert into parties (name, address)
values (:name, :address)
