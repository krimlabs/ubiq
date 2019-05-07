## Ubiq

Ubiq is an experimental way to build GraphQL apis with minimal code.

### Introduction

The central idea is to define all resolvers as a series of interceptors. These interceptors are invoked in order, can pass data down the next interceptor and can exit in case of errors. All interceptors are defined statically in `resolvers.edn` file.

Ubiq also provides components that help you tie GraphQL queries and mutations directly to SQL statements via HugSQL.


### Getting started

Ubiq assumes that you are comfortable with GraphQL, Lacinia, Lacinia-Pedestal and Integrant framework.

The difference (additional step) is the static definition of resolvers in `resolvers.edn` file.


### Debug Guide

Since Ubiq wraps your functions at various levels, the error logs might not be helpful sometimes. In such cases, make sure that:
- All interceptors used in `resolvers.edn` are imported in resolver component
- Check that the `:path` variable to `domain` interceptor is valid
