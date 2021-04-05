<?php

namespace Illuminate\Database\Eloquent {
    class Model {
        /**
         * Begin querying the model.
         *
         * @return \Illuminate\Database\Eloquent\Builder
         */
        public static function query()
        {
        }

        /**
         * Get a new query builder for the model's table.
         *
         * @return \Illuminate\Database\Eloquent\Builder
         */
        public function newQuery()
        {
        }

        /**
         * Get a new query builder that doesn't have any global scopes or eager loading.
         *
         * @return \Illuminate\Database\Eloquent\Builder|static
         */
        public function newModelQuery()
        {
        }

        /**
         * Get a new query builder with no relationships loaded.
         *
         * @return \Illuminate\Database\Eloquent\Builder
         */
        public function newQueryWithoutRelationships()
        {
        }

        /**
         * Get a new query builder that doesn't have any global scopes.
         *
         * @return \Illuminate\Database\Eloquent\Builder|static
         */
        public function newQueryWithoutScopes()
        {
        }

        /**
         * Get a new query instance without a given scope.
         *
         * @param  \Illuminate\Database\Eloquent\Scope|string  $scope
         * @return \Illuminate\Database\Eloquent\Builder
         */
        public function newQueryWithoutScope($scope)
        {
        }

        /**
         * Get a new query to restore one or more models by their queueable IDs.
         *
         * @param  array|int  $ids
         * @return \Illuminate\Database\Eloquent\Builder
         */
        public function newQueryForRestoration($ids)
        {
        }

        /**
         * Create a new Eloquent query builder for the model.
         *
         * @param  \Illuminate\Database\Query\Builder  $query
         * @return \Illuminate\Database\Eloquent\Builder|static
         */
        public function newEloquentBuilder($query)
        {
        }

        /**
         * Get a new query builder instance for the connection.
         *
         * @return \Illuminate\Database\Query\Builder
         */
        protected function newBaseQueryBuilder()
        {
        }
    }

    /**
     * @property-read HigherOrderBuilderProxy $orWhere
     *
     * @mixin \Illuminate\Database\Query\Builder
     */
    class Builder
    {
        /**
         * Create and return an un-saved model instance.
         *
         * @param  array  $attributes
         * @return \Illuminate\Database\Eloquent\Model|static
         */
        public function make(array $attributes = [])
        {
        }

        /**
         * Add a basic where clause to the query.
         *
         * @param  \Closure|string|array|\Illuminate\Database\Query\Expression  $column
         * @param  mixed  $operator
         * @param  mixed  $value
         * @param  string  $boolean
         * @return $this
         */
        public function where($column, $operator = null, $value = null, $boolean = 'and')
        {
        }

        /**
         * Add a basic where clause to the query, and return the first result.
         *
         * @param  \Closure|string|array|\Illuminate\Database\Query\Expression  $column
         * @param  mixed  $operator
         * @param  mixed  $value
         * @param  string  $boolean
         * @return \Illuminate\Database\Eloquent\Model|static
         */
        public function firstWhere($column, $operator = null, $value = null, $boolean = 'and')
        {
        }

        /**
         * Add an "or where" clause to the query.
         *
         * @param  \Closure|array|string|\Illuminate\Database\Query\Expression  $column
         * @param  mixed  $operator
         * @param  mixed  $value
         * @return $this
         */
        public function orWhere($column, $operator = null, $value = null)
        {
        }

        /**
         * Add an "order by" clause for a timestamp to the query.
         *
         * @param  string|\Illuminate\Database\Query\Expression  $column
         * @return $this
         */
        public function latest($column = null)
        {
        }

        /**
         * Add an "order by" clause for a timestamp to the query.
         *
         * @param  string|\Illuminate\Database\Query\Expression  $column
         * @return $this
         */
        public function oldest($column = null)
        {
        }

        /**
         * Find a model by its primary key.
         *
         * @param  mixed  $id
         * @param  array  $columns
         * @return \Illuminate\Database\Eloquent\Model|\Illuminate\Database\Eloquent\Collection|static[]|static|null
         */
        public function find($id, $columns = ['*'])
        {
        }

        /**
         * Find multiple models by their primary keys.
         *
         * @param  \Illuminate\Contracts\Support\Arrayable|array  $ids
         * @param  array  $columns
         * @return \Illuminate\Database\Eloquent\Collection
         */
        public function findMany($ids, $columns = ['*'])
        {
        }

        /**
         * Find a model by its primary key or throw an exception.
         *
         * @param  mixed  $id
         * @param  array  $columns
         * @return \Illuminate\Database\Eloquent\Model|\Illuminate\Database\Eloquent\Collection|static|static[]
         *
         * @throws \Illuminate\Database\Eloquent\ModelNotFoundException
         */
        public function findOrFail($id, $columns = ['*'])
        {
        }

        /**
         * Find a model by its primary key or return fresh model instance.
         *
         * @param  mixed  $id
         * @param  array  $columns
         * @return \Illuminate\Database\Eloquent\Model|static
         */
        public function findOrNew($id, $columns = ['*'])
        {
        }

        /**
         * Get the first record matching the attributes or instantiate it.
         *
         * @param  array  $attributes
         * @param  array  $values
         * @return \Illuminate\Database\Eloquent\Model|static
         */
        public function firstOrNew(array $attributes = [], array $values = [])
        {
        }

        /**
         * Get the first record matching the attributes or create it.
         *
         * @param  array  $attributes
         * @param  array  $values
         * @return \Illuminate\Database\Eloquent\Model|static
         */
        public function firstOrCreate(array $attributes = [], array $values = [])
        {
        }

        /**
         * Create or update a record matching the attributes, and fill it with values.
         *
         * @param  array  $attributes
         * @param  array  $values
         * @return \Illuminate\Database\Eloquent\Model|static
         */
        public function updateOrCreate(array $attributes, array $values = [])
        {
        }

        /**
         * Execute the query and get the first result or throw an exception.
         *
         * @param  array  $columns
         * @return \Illuminate\Database\Eloquent\Model|static
         *
         * @throws \Illuminate\Database\Eloquent\ModelNotFoundException
         */
        public function firstOrFail($columns = ['*'])
        {
        }

        /**
         * Execute the query and get the first result or call a callback.
         *
         * @param  \Closure|array  $columns
         * @param  \Closure|null  $callback
         * @return \Illuminate\Database\Eloquent\Model|static|mixed
         */
        public function firstOr($columns = ['*'], Closure $callback = null)
        {
        }

        /**
         * Execute the query and get the first result if it's the sole matching record.
         *
         * @param  array|string  $columns
         * @return \Illuminate\Database\Eloquent\Model
         *
         * @throws \Illuminate\Database\Eloquent\ModelNotFoundException
         * @throws \Illuminate\Database\MultipleRecordsFoundException
         */
        public function sole($columns = ['*'])
        {
        }

        /**
         * Get a single column's value from the first result of a query.
         *
         * @param  string|\Illuminate\Database\Query\Expression  $column
         * @return mixed
         */
        public function value($column)
        {
        }

        /**
         * Execute the query as a "select" statement.
         *
         * @param  array|string  $columns
         * @return \Illuminate\Database\Eloquent\Collection|static[]
         */
        public function get($columns = ['*'])
        {
        }

        /**
         * Get the hydrated models without eager loading.
         *
         * @param  array|string  $columns
         * @return \Illuminate\Database\Eloquent\Model[]|static[]
         */
        public function getModels($columns = ['*'])
        {
        }

        /**
         * Get the relation instance for the given relation name.
         *
         * @param  string  $name
         * @return \Illuminate\Database\Eloquent\Relations\Relation
         */
        public function getRelation($name)
        {
        }

        /**
         * Get an array with the values of a given column.
         *
         * @param  string|\Illuminate\Database\Query\Expression  $column
         * @param  string|null  $key
         * @return \Illuminate\Support\Collection
         */
        public function pluck($column, $key = null)
        {
        }

        /**
         * Paginate the given query.
         *
         * @param  int|null  $perPage
         * @param  array  $columns
         * @param  string  $pageName
         * @param  int|null  $page
         * @return \Illuminate\Contracts\Pagination\LengthAwarePaginator
         *
         * @throws \InvalidArgumentException
         */
        public function paginate($perPage = null, $columns = ['*'], $pageName = 'page', $page = null)
        {
        }

        /**
         * Paginate the given query into a simple paginator.
         *
         * @param  int|null  $perPage
         * @param  array  $columns
         * @param  string  $pageName
         * @param  int|null  $page
         * @return \Illuminate\Contracts\Pagination\Paginator
         */
        public function simplePaginate($perPage = null, $columns = ['*'], $pageName = 'page', $page = null)
        {
        }

        /**
         * Save a new model and return the instance.
         *
         * @param  array  $attributes
         * @return \Illuminate\Database\Eloquent\Model|$this
         */
        public function create(array $attributes = [])
        {
        }

        /**
         * Save a new model and return the instance. Allow mass-assignment.
         *
         * @param  array  $attributes
         * @return \Illuminate\Database\Eloquent\Model|$this
         */
        public function forceCreate(array $attributes)
        {
        }

        /**
         * Update records in the database.
         *
         * @param  array  $values
         * @return int
         */
        public function update(array $values)
        {
        }

        /**
         * Insert new records or update the existing ones.
         *
         * @param  array  $values
         * @param  array|string  $uniqueBy
         * @param  array|null  $update
         * @return int
         */
        public function upsert(array $values, $uniqueBy, $update = null)
        {
        }

        /**
         * Increment a column's value by a given amount.
         *
         * @param  string|\Illuminate\Database\Query\Expression  $column
         * @param  float|int  $amount
         * @param  array  $extra
         * @return int
         */
        public function increment($column, $amount = 1, array $extra = [])
        {
        }

        /**
         * Decrement a column's value by a given amount.
         *
         * @param  string|\Illuminate\Database\Query\Expression  $column
         * @param  float|int  $amount
         * @param  array  $extra
         * @return int
         */
        public function decrement($column, $amount = 1, array $extra = [])
        {
        }

        /**
         * Set the relationships that should be eager loaded.
         *
         * @param  string|array  $relations
         * @param  string|\Closure|null  $callback
         * @return $this
         */
        public function with($relations, $callback = null)
        {
        }

        /**
         * Prevent the specified relations from being eager loaded.
         *
         * @param  mixed  $relations
         * @return $this
         */
        public function without($relations)
        {
        }

        /**
         * Get the underlying query builder instance.
         *
         * @return \Illuminate\Database\Query\Builder
         */
        public function getQuery()
        {
        }

        /**
         * Get a base query builder instance.
         *
         * @return \Illuminate\Database\Query\Builder
         */
        public function toBase()
        {
        }

        /**
         * Qualify the given column name by the model's table.
         *
         * @param  string|\Illuminate\Database\Query\Expression  $column
         * @return string
         */
        public function qualifyColumn($column)
        {
        }
    }
}

namespace Illuminate\Database\Eloquent\Relations {
    class Relation {
    }
}


namespace Illuminate\Database\Query {
class Builder
{
    /**
     * All of the available clause operators.
     *
     * @var array
     */
    public $operators = [
        '=', '<', '>', '<=', '>=', '<>', '!=', '<=>',
        'like', 'like binary', 'not like', 'ilike',
        '&', '|', '^', '<<', '>>',
        'rlike', 'not rlike', 'regexp', 'not regexp',
        '~', '~*', '!~', '!~*', 'similar to',
        'not similar to', 'not ilike', '~~*', '!~~*',
    ];

    /**
     * Set the columns to be selected.
     *
     * @param  array|mixed  $columns
     * @return $this
     */
    public function select($columns = ['*'])
    {
    }

    /**
     * Add a new select column to the query.
     *
     * @param  array|mixed  $column
     * @return $this
     */
    public function addSelect($column)
    {
    }

    /**
     * Set the table which the query is targeting.
     *
     * @param  \Closure|\Illuminate\Database\Query\Builder|string  $table
     * @param  string|null  $as
     * @return $this
     */
    public function from($table, $as = null)
    {
    }

    /**
     * Add a join clause to the query.
     *
     * @param  string  $table
     * @param  \Closure|string  $first
     * @param  string|null  $operator
     * @param  string|null  $second
     * @param  string  $type
     * @param  bool  $where
     * @return $this
     */
    public function join($table, $first, $operator = null, $second = null, $type = 'inner', $where = false)
    {
    }

    /**
     * Add a "join where" clause to the query.
     *
     * @param  string  $table
     * @param  \Closure|string  $first
     * @param  string  $operator
     * @param  string  $second
     * @param  string  $type
     * @return $this
     */
    public function joinWhere($table, $first, $operator, $second, $type = 'inner')
    {
    }

    /**
     * Add a subquery join clause to the query.
     *
     * @param  \Closure|\Illuminate\Database\Query\Builder|string  $query
     * @param  string  $as
     * @param  \Closure|string  $first
     * @param  string|null  $operator
     * @param  string|null  $second
     * @param  string  $type
     * @param  bool  $where
     * @return $this
     *
     * @throws \InvalidArgumentException
     */
    public function joinSub($query, $as, $first, $operator = null, $second = null, $type = 'inner', $where = false)
    {
    }

    /**
     * Add a left join to the query.
     *
     * @param  string  $table
     * @param  \Closure|string  $first
     * @param  string|null  $operator
     * @param  string|null  $second
     * @return $this
     */
    public function leftJoin($table, $first, $operator = null, $second = null)
    {
    }

    /**
     * Add a "join where" clause to the query.
     *
     * @param  string  $table
     * @param  \Closure|string  $first
     * @param  string  $operator
     * @param  string  $second
     * @return $this
     */
    public function leftJoinWhere($table, $first, $operator, $second)
    {
    }

    /**
     * Add a subquery left join to the query.
     *
     * @param  \Closure|\Illuminate\Database\Query\Builder|string  $query
     * @param  string  $as
     * @param  \Closure|string  $first
     * @param  string|null  $operator
     * @param  string|null  $second
     * @return $this
     */
    public function leftJoinSub($query, $as, $first, $operator = null, $second = null)
    {
    }

    /**
     * Add a right join to the query.
     *
     * @param  string  $table
     * @param  \Closure|string  $first
     * @param  string|null  $operator
     * @param  string|null  $second
     * @return $this
     */
    public function rightJoin($table, $first, $operator = null, $second = null)
    {
    }

    /**
     * Add a "right join where" clause to the query.
     *
     * @param  string  $table
     * @param  \Closure|string  $first
     * @param  string  $operator
     * @param  string  $second
     * @return $this
     */
    public function rightJoinWhere($table, $first, $operator, $second)
    {
    }

    /**
     * Add a subquery right join to the query.
     *
     * @param  \Closure|\Illuminate\Database\Query\Builder|string  $query
     * @param  string  $as
     * @param  \Closure|string  $first
     * @param  string|null  $operator
     * @param  string|null  $second
     * @return $this
     */
    public function rightJoinSub($query, $as, $first, $operator = null, $second = null)
    {
    }

    /**
     * Add a "cross join" clause to the query.
     *
     * @param  string  $table
     * @param  \Closure|string|null  $first
     * @param  string|null  $operator
     * @param  string|null  $second
     * @return $this
     */
    public function crossJoin($table, $first = null, $operator = null, $second = null)
    {
    }

    /**
     * Add a basic where clause to the query.
     *
     * @param  \Closure|string|array  $column
     * @param  mixed  $operator
     * @param  mixed  $value
     * @param  string  $boolean
     * @return $this
     */
    public function where($column, $operator = null, $value = null, $boolean = 'and')
    {
    }

    /**
     * Add an "or where" clause to the query.
     *
     * @param  \Closure|string|array  $column
     * @param  mixed  $operator
     * @param  mixed  $value
     * @return $this
     */
    public function orWhere($column, $operator = null, $value = null)
    {
    }

    /**
     * Add a "where" clause comparing two columns to the query.
     *
     * @param  string|array  $first
     * @param  string|null  $operator
     * @param  string|null  $second
     * @param  string|null  $boolean
     * @return $this
     */
    public function whereColumn($first, $operator = null, $second = null, $boolean = 'and')
    {
    }

    /**
     * Add an "or where" clause comparing two columns to the query.
     *
     * @param  string|array  $first
     * @param  string|null  $operator
     * @param  string|null  $second
     * @return $this
     */
    public function orWhereColumn($first, $operator = null, $second = null)
    {
    }

    /**
     * Add a "where in" clause to the query.
     *
     * @param  string  $column
     * @param  mixed  $values
     * @param  string  $boolean
     * @param  bool  $not
     * @return $this
     */
    public function whereIn($column, $values, $boolean = 'and', $not = false)
    {
    }

    /**
     * Add an "or where in" clause to the query.
     *
     * @param  string  $column
     * @param  mixed  $values
     * @return $this
     */
    public function orWhereIn($column, $values)
    {
    }

    /**
     * Add a "where not in" clause to the query.
     *
     * @param  string  $column
     * @param  mixed  $values
     * @param  string  $boolean
     * @return $this
     */
    public function whereNotIn($column, $values, $boolean = 'and')
    {
    }

    /**
     * Add an "or where not in" clause to the query.
     *
     * @param  string  $column
     * @param  mixed  $values
     * @return $this
     */
    public function orWhereNotIn($column, $values)
    {
    }

    /**
     * Add a "where in raw" clause for integer values to the query.
     *
     * @param  string  $column
     * @param  \Illuminate\Contracts\Support\Arrayable|array  $values
     * @param  string  $boolean
     * @param  bool  $not
     * @return $this
     */
    public function whereIntegerInRaw($column, $values, $boolean = 'and', $not = false)
    {
    }

    /**
     * Add an "or where in raw" clause for integer values to the query.
     *
     * @param  string  $column
     * @param  \Illuminate\Contracts\Support\Arrayable|array  $values
     * @return $this
     */
    public function orWhereIntegerInRaw($column, $values)
    {
    }

    /**
     * Add a "where not in raw" clause for integer values to the query.
     *
     * @param  string  $column
     * @param  \Illuminate\Contracts\Support\Arrayable|array  $values
     * @param  string  $boolean
     * @return $this
     */
    public function whereIntegerNotInRaw($column, $values, $boolean = 'and')
    {
    }

    /**
     * Add an "or where not in raw" clause for integer values to the query.
     *
     * @param  string  $column
     * @param  \Illuminate\Contracts\Support\Arrayable|array  $values
     * @return $this
     */
    public function orWhereIntegerNotInRaw($column, $values)
    {
    }

    /**
     * Add a "where null" clause to the query.
     *
     * @param  string|array  $columns
     * @param  string  $boolean
     * @param  bool  $not
     * @return $this
     */
    public function whereNull($columns, $boolean = 'and', $not = false)
    {
    }

    /**
     * Add an "or where null" clause to the query.
     *
     * @param  string  $column
     * @return $this
     */
    public function orWhereNull($column)
    {
    }

    /**
     * Add a "where not null" clause to the query.
     *
     * @param  string|array  $columns
     * @param  string  $boolean
     * @return $this
     */
    public function whereNotNull($columns, $boolean = 'and')
    {
    }

    /**
     * Add a where between statement to the query.
     *
     * @param  string  $column
     * @param  array  $values
     * @param  string  $boolean
     * @param  bool  $not
     * @return $this
     */
    public function whereBetween($column, array $values, $boolean = 'and', $not = false)
    {
    }

    /**
     * Add a where between statement using columns to the query.
     *
     * @param  string  $column
     * @param  array  $values
     * @param  string  $boolean
     * @param  bool  $not
     * @return $this
     */
    public function whereBetweenColumns($column, array $values, $boolean = 'and', $not = false)
    {
    }

    /**
     * Add an or where between statement to the query.
     *
     * @param  string  $column
     * @param  array  $values
     * @return $this
     */
    public function orWhereBetween($column, array $values)
    {
    }

    /**
     * Add an or where between statement using columns to the query.
     *
     * @param  string  $column
     * @param  array  $values
     * @return $this
     */
    public function orWhereBetweenColumns($column, array $values)
    {
    }

    /**
     * Add a where not between statement to the query.
     *
     * @param  string  $column
     * @param  array  $values
     * @param  string  $boolean
     * @return $this
     */
    public function whereNotBetween($column, array $values, $boolean = 'and')
    {
    }

    /**
     * Add a where not between statement using columns to the query.
     *
     * @param  string  $column
     * @param  array  $values
     * @param  string  $boolean
     * @return $this
     */
    public function whereNotBetweenColumns($column, array $values, $boolean = 'and')
    {
    }

    /**
     * Add an or where not between statement to the query.
     *
     * @param  string  $column
     * @param  array  $values
     * @return $this
     */
    public function orWhereNotBetween($column, array $values)
    {
    }

    /**
     * Add an or where not between statement using columns to the query.
     *
     * @param  string  $column
     * @param  array  $values
     * @return $this
     */
    public function orWhereNotBetweenColumns($column, array $values)
    {
    }

    /**
     * Add an "or where not null" clause to the query.
     *
     * @param  string  $column
     * @return $this
     */
    public function orWhereNotNull($column)
    {
    }

    /**
     * Add a "where date" statement to the query.
     *
     * @param  string  $column
     * @param  string  $operator
     * @param  \DateTimeInterface|string|null  $value
     * @param  string  $boolean
     * @return $this
     */
    public function whereDate($column, $operator, $value = null, $boolean = 'and')
    {
    }

    /**
     * Add an "or where date" statement to the query.
     *
     * @param  string  $column
     * @param  string  $operator
     * @param  \DateTimeInterface|string|null  $value
     * @return $this
     */
    public function orWhereDate($column, $operator, $value = null)
    {
    }

    /**
     * Add a "where time" statement to the query.
     *
     * @param  string  $column
     * @param  string  $operator
     * @param  \DateTimeInterface|string|null  $value
     * @param  string  $boolean
     * @return $this
     */
    public function whereTime($column, $operator, $value = null, $boolean = 'and')
    {
    }

    /**
     * Add an "or where time" statement to the query.
     *
     * @param  string  $column
     * @param  string  $operator
     * @param  \DateTimeInterface|string|null  $value
     * @return $this
     */
    public function orWhereTime($column, $operator, $value = null)
    {
    }

    /**
     * Add a "where day" statement to the query.
     *
     * @param  string  $column
     * @param  string  $operator
     * @param  \DateTimeInterface|string|null  $value
     * @param  string  $boolean
     * @return $this
     */
    public function whereDay($column, $operator, $value = null, $boolean = 'and')
    {
    }

    /**
     * Add an "or where day" statement to the query.
     *
     * @param  string  $column
     * @param  string  $operator
     * @param  \DateTimeInterface|string|null  $value
     * @return $this
     */
    public function orWhereDay($column, $operator, $value = null)
    {
    }

    /**
     * Add a "where month" statement to the query.
     *
     * @param  string  $column
     * @param  string  $operator
     * @param  \DateTimeInterface|string|null  $value
     * @param  string  $boolean
     * @return $this
     */
    public function whereMonth($column, $operator, $value = null, $boolean = 'and')
    {
    }

    /**
     * Add an "or where month" statement to the query.
     *
     * @param  string  $column
     * @param  string  $operator
     * @param  \DateTimeInterface|string|null  $value
     * @return $this
     */
    public function orWhereMonth($column, $operator, $value = null)
    {
    }

    /**
     * Add a "where year" statement to the query.
     *
     * @param  string  $column
     * @param  string  $operator
     * @param  \DateTimeInterface|string|int|null  $value
     * @param  string  $boolean
     * @return $this
     */
    public function whereYear($column, $operator, $value = null, $boolean = 'and')
    {
    }

    /**
     * Add an "or where year" statement to the query.
     *
     * @param  string  $column
     * @param  string  $operator
     * @param  \DateTimeInterface|string|int|null  $value
     * @return $this
     */
    public function orWhereYear($column, $operator, $value = null)
    {
    }

    /**
     * Adds a where condition using row values.
     *
     * @param  array  $columns
     * @param  string  $operator
     * @param  array  $values
     * @param  string  $boolean
     * @return $this
     *
     * @throws \InvalidArgumentException
     */
    public function whereRowValues($columns, $operator, $values, $boolean = 'and')
    {
    }

    /**
     * Adds an or where condition using row values.
     *
     * @param  array  $columns
     * @param  string  $operator
     * @param  array  $values
     * @return $this
     */
    public function orWhereRowValues($columns, $operator, $values)
    {
    }

    /**
     * Add a "where JSON contains" clause to the query.
     *
     * @param  string  $column
     * @param  mixed  $value
     * @param  string  $boolean
     * @param  bool  $not
     * @return $this
     */
    public function whereJsonContains($column, $value, $boolean = 'and', $not = false)
    {
    }

    /**
     * Add an "or where JSON contains" clause to the query.
     *
     * @param  string  $column
     * @param  mixed  $value
     * @return $this
     */
    public function orWhereJsonContains($column, $value)
    {
    }

    /**
     * Add a "where JSON not contains" clause to the query.
     *
     * @param  string  $column
     * @param  mixed  $value
     * @param  string  $boolean
     * @return $this
     */
    public function whereJsonDoesntContain($column, $value, $boolean = 'and')
    {
    }

    /**
     * Add an "or where JSON not contains" clause to the query.
     *
     * @param  string  $column
     * @param  mixed  $value
     * @return $this
     */
    public function orWhereJsonDoesntContain($column, $value)
    {
    }

    /**
     * Add a "where JSON length" clause to the query.
     *
     * @param  string  $column
     * @param  mixed  $operator
     * @param  mixed  $value
     * @param  string  $boolean
     * @return $this
     */
    public function whereJsonLength($column, $operator, $value = null, $boolean = 'and')
    {
    }

    /**
     * Add an "or where JSON length" clause to the query.
     *
     * @param  string  $column
     * @param  mixed  $operator
     * @param  mixed  $value
     * @return $this
     */
    public function orWhereJsonLength($column, $operator, $value = null)
    {
    }

    /**
     * Add a "group by" clause to the query.
     *
     * @param  array|string  ...$groups
     * @return $this
     */
    public function groupBy(...$groups)
    {
    }

    /**
     * Add a "having" clause to the query.
     *
     * @param  string  $column
     * @param  string|null  $operator
     * @param  string|null  $value
     * @param  string  $boolean
     * @return $this
     */
    public function having($column, $operator = null, $value = null, $boolean = 'and')
    {
    }

    /**
     * Add an "or having" clause to the query.
     *
     * @param  string  $column
     * @param  string|null  $operator
     * @param  string|null  $value
     * @return $this
     */
    public function orHaving($column, $operator = null, $value = null)
    {
    }

    /**
     * Add a "having between " clause to the query.
     *
     * @param  string  $column
     * @param  array  $values
     * @param  string  $boolean
     * @param  bool  $not
     * @return $this
     */
    public function havingBetween($column, array $values, $boolean = 'and', $not = false)
    {
    }

    /**
     * Add an "order by" clause to the query.
     *
     * @param  \Closure|\Illuminate\Database\Query\Builder|\Illuminate\Database\Query\Expression|string  $column
     * @param  string  $direction
     * @return $this
     *
     * @throws \InvalidArgumentException
     */
    public function orderBy($column, $direction = 'asc')
    {
    }

    /**
     * Add a descending "order by" clause to the query.
     *
     * @param  string  $column
     * @return $this
     */
    public function orderByDesc($column)
    {
    }

    /**
     * Add an "order by" clause for a timestamp to the query.
     *
     * @param  string  $column
     * @return $this
     */
    public function latest($column = 'created_at')
    {
    }

    /**
     * Add an "order by" clause for a timestamp to the query.
     *
     * @param  string  $column
     * @return $this
     */
    public function oldest($column = 'created_at')
    {
    }

    /**
     * Constrain the query to the previous "page" of results before a given ID.
     *
     * @param  int  $perPage
     * @param  int|null  $lastId
     * @param  string  $column
     * @return $this
     */
    public function forPageBeforeId($perPage = 15, $lastId = 0, $column = 'id')
    {
    }

    /**
     * Constrain the query to the next "page" of results after a given ID.
     *
     * @param  int  $perPage
     * @param  int|null  $lastId
     * @param  string  $column
     * @return $this
     */
    public function forPageAfterId($perPage = 15, $lastId = 0, $column = 'id')
    {
    }

    /**
     * Remove all existing orders and optionally add a new order.
     *
     * @param  string|null  $column
     * @param  string  $direction
     * @return $this
     */
    public function reorder($column = null, $direction = 'asc')
    {
    }

    /**
     * Execute a query for a single record by ID.
     *
     * @param  int|string  $id
     * @param  array  $columns
     * @return mixed|static
     */
    public function find($id, $columns = ['*'])
    {
    }

    /**
     * Get a single column's value from the first result of a query.
     *
     * @param  string  $column
     * @return mixed
     */
    public function value($column)
    {
    }

    /**
     * Execute the query as a "select" statement.
     *
     * @param  array|string  $columns
     * @return \Illuminate\Support\Collection
     */
    public function get($columns = ['*'])
    {
    }

    /**
     * Paginate the given query into a simple paginator.
     *
     * @param  int  $perPage
     * @param  array  $columns
     * @param  string  $pageName
     * @param  int|null  $page
     * @return \Illuminate\Contracts\Pagination\LengthAwarePaginator
     */
    public function paginate($perPage = 15, $columns = ['*'], $pageName = 'page', $page = null)
    {
    }

    /**
     * Get a paginator only supporting simple next and previous links.
     *
     * This is more efficient on larger data-sets, etc.
     *
     * @param  int  $perPage
     * @param  array  $columns
     * @param  string  $pageName
     * @param  int|null  $page
     * @return \Illuminate\Contracts\Pagination\Paginator
     */
    public function simplePaginate($perPage = 15, $columns = ['*'], $pageName = 'page', $page = null)
    {
    }

    /**
     * Get the count of the total records for the paginator.
     *
     * @param  array  $columns
     * @return int
     */
    public function getCountForPagination($columns = ['*'])
    {
    }

    /**
     * Get an array with the values of a given column.
     *
     * @param  string  $column
     * @param  string|null  $key
     * @return \Illuminate\Support\Collection
     */
    public function pluck($column, $key = null)
    {
    }

    /**
     * Concatenate values of a given column as a string.
     *
     * @param  string  $column
     * @param  string  $glue
     * @return string
     */
    public function implode($column, $glue = '')
    {
    }

    /**
     * Retrieve the "count" result of the query.
     *
     * @param  string  $columns
     * @return int
     */
    public function count($columns = '*')
    {
    }

    /**
     * Retrieve the minimum value of a given column.
     *
     * @param  string  $column
     * @return mixed
     */
    public function min($column)
    {
    }

    /**
     * Retrieve the maximum value of a given column.
     *
     * @param  string  $column
     * @return mixed
     */
    public function max($column)
    {
    }

    /**
     * Retrieve the sum of the values of a given column.
     *
     * @param  string  $column
     * @return mixed
     */
    public function sum($column)
    {
    }

    /**
     * Retrieve the average of the values of a given column.
     *
     * @param  string  $column
     * @return mixed
     */
    public function avg($column)
    {
    }

    /**
     * Alias for the "avg" method.
     *
     * @param  string  $column
     * @return mixed
     */
    public function average($column)
    {
    }

    /**
     * Execute an aggregate function on the database.
     *
     * @param  string  $function
     * @param  array  $columns
     * @return mixed
     */
    public function aggregate($function, $columns = ['*'])
    {
    }

    /**
     * Execute a numeric aggregate function on the database.
     *
     * @param  string  $function
     * @param  array  $columns
     * @return float|int
     */
    public function numericAggregate($function, $columns = ['*'])
    {
    }

    /**
     * Insert a new record into the database.
     *
     * @param  array  $values
     * @return bool
     */
    public function insert(array $values)
    {
    }

    /**
     * Insert a new record into the database while ignoring errors.
     *
     * @param  array  $values
     * @return int
     */
    public function insertOrIgnore(array $values)
    {
    }

    /**
     * Insert a new record and get the value of the primary key.
     *
     * @param  array  $values
     * @param  string|null  $sequence
     * @return int
     */
    public function insertGetId(array $values, $sequence = null)
    {
    }

    /**
     * Insert new records into the table using a subquery.
     *
     * @param  array  $columns
     * @param  \Closure|\Illuminate\Database\Query\Builder|string  $query
     * @return int
     */
    public function insertUsing(array $columns, $query)
    {
    }

    /**
     * Update a record in the database.
     *
     * @param  array  $values
     * @return int
     */
    public function update(array $values)
    {
    }

    /**
     * Insert or update a record matching the attributes, and fill it with values.
     *
     * @param  array  $attributes
     * @param  array  $values
     * @return bool
     */
    public function updateOrInsert(array $attributes, array $values = [])
    {
    }

    /**
     * Increment a column's value by a given amount.
     *
     * @param  string  $column
     * @param  float|int  $amount
     * @param  array  $extra
     * @return int
     *
     * @throws \InvalidArgumentException
     */
    public function increment($column, $amount = 1, array $extra = [])
    {
    }

    /**
     * Decrement a column's value by a given amount.
     *
     * @param  string  $column
     * @param  float|int  $amount
     * @param  array  $extra
     * @return int
     *
     * @throws \InvalidArgumentException
     */
    public function decrement($column, $amount = 1, array $extra = [])
    {
    }

    /**
     * Get a new instance of the query builder.
     *
     * @return \Illuminate\Database\Query\Builder
     */
    public function newQuery()
    {
    }

    /**
     * Create a raw database expression.
     *
     * @param  mixed  $value
     * @return \Illuminate\Database\Query\Expression
     */
    public function raw($value)
    {
    }

    /**
     * Handle dynamic method calls into the method.
     *
     * @param  string  $method
     * @param  array  $parameters
     * @return mixed
     *
     * @throws \BadMethodCallException
     */
    public function __call($method, $parameters)
    {
    }
}

class JoinClause extends Builder {
    /**
     * Add an "on" clause to the join.
     *
     * On clauses can be chained, e.g.
     *
     *  $join->on('contacts.user_id', '=', 'users.id')
     *       ->on('contacts.info_id', '=', 'info.id')
     *
     * will produce the following SQL:
     *
     * on `contacts`.`user_id` = `users`.`id` and `contacts`.`info_id` = `info`.`id`
     *
     * @param  \Closure|string  $first
     * @param  string|null  $operator
     * @param  \Illuminate\Database\Query\Expression|string|null  $second
     * @param  string  $boolean
     * @return $this
     *
     * @throws \InvalidArgumentException
     */
    public function on($first, $operator = null, $second = null, $boolean = 'and')
    {
    }

    /**
     * Add an "or on" clause to the join.
     *
     * @param  \Closure|string  $first
     * @param  string|null  $operator
     * @param  string|null  $second
     * @return \Illuminate\Database\Query\JoinClause
     */
    public function orOn($first, $operator = null, $second = null)
    {
    }

    /**
     * Get a new instance of the join clause builder.
     *
     * @return \Illuminate\Database\Query\JoinClause
     */
    public function newQuery()
    {
    }
}
}

namespace Illuminate\Database\Schema {
class Builder {
    /**
     * Create a database in the schema.
     *
     * @param string $name
     * @return bool
     * @static
     */
    public static function createDatabase($name)
    {
    }

    /**
     * Drop a database from the schema if the database exists.
     *
     * @param string $name
     * @return bool
     * @static
     */
    public static function dropDatabaseIfExists($name)
    {
    }

    /**
     * Determine if the given table exists.
     *
     * @param string $table
     * @return bool
     * @static
     */
    public static function hasTable($table)
    {
    }

    /**
     * Get the column listing for a given table.
     *
     * @param string $table
     * @return array
     * @static
     */
    public static function getColumnListing($table)
    {
    }

    /**
     * Determine if the given table has a given column.
     *
     * @param string $table
     * @param string $column
     * @return bool
     * @static
     */
    public static function hasColumn($table, $column)
    {
    }

    /**
     * Determine if the given table has given columns.
     *
     * @param string $table
     * @param array $columns
     * @return bool
     * @static
     */
    public static function hasColumns($table, $columns)
    {
    }

    /**
     * Get the data type for the given column name.
     *
     * @param string $table
     * @param string $column
     * @return string
     * @static
     */
    public static function getColumnType($table, $column)
    {
    }

    /**
     * Modify a table on the schema.
     *
     * @param string $table
     * @param \Closure $callback
     * @return void
     * @static
     */
    public static function table($table, $callback)
    {
    }

    /**
     * Create a new table on the schema.
     *
     * @param string $table
     * @param \Closure $callback
     * @return void
     * @static
     */
    public static function create($table, $callback)
    {
    }

    /**
     * Drop a table from the schema.
     *
     * @param string $table
     * @return void
     * @static
     */
    public static function drop($table)
    {
    }

    /**
     * Drop a table from the schema if it exists.
     *
     * @param string $table
     * @return void
     * @static
     */
    public static function dropIfExists($table)
    {
    }

    /**
     * Drop columns from a table schema.
     *
     * @param string $table
     * @param string|array $columns
     * @return void
     * @static
     */
    public static function dropColumns($table, $columns)
    {
    }

    /**
     * Rename a table on the schema.
     *
     * @param string $from
     * @param string $to
     * @return void
     * @static
     */
    public static function rename($from, $to)
    {
    }
}

class Blueprint {
    /**
     * Indicate that the given columns should be dropped.
     *
     * @param  array|mixed  $columns
     * @return \Illuminate\Support\Fluent
     */
    public function dropColumn($columns)
    {
    }

    /**
     * Indicate that the given columns should be renamed.
     *
     * @param  string  $from
     * @param  string  $to
     * @return \Illuminate\Support\Fluent
     */
    public function renameColumn($from, $to)
    {
    }

    /**
     * Indicate that the given primary key should be dropped.
     *
     * @param  string|array|null  $index
     * @return \Illuminate\Support\Fluent
     */
    public function dropPrimary($index = null)
    {
    }

    /**
     * Indicate that the given unique key should be dropped.
     *
     * @param  string|array  $index
     * @return \Illuminate\Support\Fluent
     */
    public function dropUnique($index)
    {
    }

    /**
     * Indicate that the given index should be dropped.
     *
     * @param  string|array  $index
     * @return \Illuminate\Support\Fluent
     */
    public function dropIndex($index)
    {
    }

    /**
     * Indicate that the given spatial index should be dropped.
     *
     * @param  string|array  $index
     * @return \Illuminate\Support\Fluent
     */
    public function dropSpatialIndex($index)
    {
    }

    /**
     * Indicate that the given foreign key should be dropped.
     *
     * @param  string|array  $index
     * @return \Illuminate\Support\Fluent
     */
    public function dropForeign($index)
    {
    }

    /**
     * Indicate that the given column and foreign key should be dropped.
     *
     * @param  string  $column
     * @return \Illuminate\Support\Fluent
     */
    public function dropConstrainedForeignId($column)
    {
    }

    /**
     * Indicate that the given indexes should be renamed.
     *
     * @param  string  $from
     * @param  string  $to
     * @return \Illuminate\Support\Fluent
     */
    public function renameIndex($from, $to)
    {
    }

    /**
     * Indicate that the soft delete column should be dropped.
     *
     * @param  string  $column
     * @return void
     */
    public function dropSoftDeletes($column = 'deleted_at')
    {
    }

    /**
     * Indicate that the soft delete column should be dropped.
     *
     * @param  string  $column
     * @return void
     */
    public function dropSoftDeletesTz($column = 'deleted_at')
    {
    }

    /**
     * Indicate that the polymorphic columns should be dropped.
     *
     * @param  string  $name
     * @param  string|null  $indexName
     * @return void
     */
    public function dropMorphs($name, $indexName = null)
    {
        $this->dropIndex($indexName ?: $this->createIndexName('index', ["{$name}_type", "{$name}_id"]));
        $this->dropColumn("{$name}_type", "{$name}_id");
    }

    /**
     * Rename the table to a given name.
     *
     * @param  string  $to
     * @return \Illuminate\Support\Fluent
     */
    public function rename($to)
    {
    }

    /**
     * Specify the primary key(s) for the table.
     *
     * @param  string|array  $columns
     * @param  string|null  $name
     * @param  string|null  $algorithm
     * @return \Illuminate\Support\Fluent
     */
    public function primary($columns, $name = null, $algorithm = null)
    {
    }

    /**
     * Specify a unique index for the table.
     *
     * @param  string|array  $columns
     * @param  string|null  $name
     * @param  string|null  $algorithm
     * @return \Illuminate\Support\Fluent
     */
    public function unique($columns, $name = null, $algorithm = null)
    {
    }

    /**
     * Specify an index for the table.
     *
     * @param  string|array  $columns
     * @param  string|null  $name
     * @param  string|null  $algorithm
     * @return \Illuminate\Support\Fluent
     */
    public function index($columns, $name = null, $algorithm = null)
    {
    }

    /**
     * Specify a spatial index for the table.
     *
     * @param  string|array  $columns
     * @param  string|null  $name
     * @return \Illuminate\Support\Fluent
     */
    public function spatialIndex($columns, $name = null)
    {
    }

    /**
     * Specify a raw index for the table.
     *
     * @param  string  $expression
     * @param  string  $name
     * @return \Illuminate\Support\Fluent
     */
    public function rawIndex($expression, $name)
    {
    }

    /**
     * Specify a foreign key for the table.
     *
     * @param  string|array  $columns
     * @param  string|null  $name
     * @return \Illuminate\Database\Schema\ForeignKeyDefinition
     */
    public function foreign($columns, $name = null)
    {
    }

    /**
     * Create a new auto-incrementing big integer (8-byte) column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function id($column = 'id')
    {
    }

    /**
     * Create a new auto-incrementing integer (4-byte) column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function increments($column)
    {
    }

    /**
     * Create a new auto-incrementing integer (4-byte) column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function integerIncrements($column)
    {
    }

    /**
     * Create a new auto-incrementing tiny integer (1-byte) column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function tinyIncrements($column)
    {
    }

    /**
     * Create a new auto-incrementing small integer (2-byte) column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function smallIncrements($column)
    {
    }

    /**
     * Create a new auto-incrementing medium integer (3-byte) column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function mediumIncrements($column)
    {
    }

    /**
     * Create a new auto-incrementing big integer (8-byte) column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function bigIncrements($column)
    {
    }

    /**
     * Create a new char column on the table.
     *
     * @param  string  $column
     * @param  int|null  $length
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function char($column, $length = null)
    {
    }

    /**
     * Create a new string column on the table.
     *
     * @param  string  $column
     * @param  int|null  $length
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function string($column, $length = null)
    {
    }

    /**
     * Create a new text column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function text($column)
    {
    }

    /**
     * Create a new medium text column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function mediumText($column)
    {
    }

    /**
     * Create a new long text column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function longText($column)
    {
    }

    /**
     * Create a new integer (4-byte) column on the table.
     *
     * @param  string  $column
     * @param  bool  $autoIncrement
     * @param  bool  $unsigned
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function integer($column, $autoIncrement = false, $unsigned = false)
    {
    }

    /**
     * Create a new tiny integer (1-byte) column on the table.
     *
     * @param  string  $column
     * @param  bool  $autoIncrement
     * @param  bool  $unsigned
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function tinyInteger($column, $autoIncrement = false, $unsigned = false)
    {
    }

    /**
     * Create a new small integer (2-byte) column on the table.
     *
     * @param  string  $column
     * @param  bool  $autoIncrement
     * @param  bool  $unsigned
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function smallInteger($column, $autoIncrement = false, $unsigned = false)
    {
    }

    /**
     * Create a new medium integer (3-byte) column on the table.
     *
     * @param  string  $column
     * @param  bool  $autoIncrement
     * @param  bool  $unsigned
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function mediumInteger($column, $autoIncrement = false, $unsigned = false)
    {
    }

    /**
     * Create a new big integer (8-byte) column on the table.
     *
     * @param  string  $column
     * @param  bool  $autoIncrement
     * @param  bool  $unsigned
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function bigInteger($column, $autoIncrement = false, $unsigned = false)
    {
    }

    /**
     * Create a new unsigned integer (4-byte) column on the table.
     *
     * @param  string  $column
     * @param  bool  $autoIncrement
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function unsignedInteger($column, $autoIncrement = false)
    {
    }

    /**
     * Create a new unsigned tiny integer (1-byte) column on the table.
     *
     * @param  string  $column
     * @param  bool  $autoIncrement
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function unsignedTinyInteger($column, $autoIncrement = false)
    {
    }

    /**
     * Create a new unsigned small integer (2-byte) column on the table.
     *
     * @param  string  $column
     * @param  bool  $autoIncrement
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function unsignedSmallInteger($column, $autoIncrement = false)
    {
    }

    /**
     * Create a new unsigned medium integer (3-byte) column on the table.
     *
     * @param  string  $column
     * @param  bool  $autoIncrement
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function unsignedMediumInteger($column, $autoIncrement = false)
    {
    }

    /**
     * Create a new unsigned big integer (8-byte) column on the table.
     *
     * @param  string  $column
     * @param  bool  $autoIncrement
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function unsignedBigInteger($column, $autoIncrement = false)
    {
    }

    /**
     * Create a new unsigned big integer (8-byte) column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ForeignIdColumnDefinition
     */
    public function foreignId($column)
    {
    }

    /**
     * Create a foreign ID column for the given model.
     *
     * @param  \Illuminate\Database\Eloquent\Model|string  $model
     * @param  string|null  $column
     * @return \Illuminate\Database\Schema\ForeignIdColumnDefinition
     */
    public function foreignIdFor($model, $column = null)
    {
    }

    /**
     * Create a new float column on the table.
     *
     * @param  string  $column
     * @param  int  $total
     * @param  int  $places
     * @param  bool  $unsigned
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function float($column, $total = 8, $places = 2, $unsigned = false)
    {
    }

    /**
     * Create a new double column on the table.
     *
     * @param  string  $column
     * @param  int|null  $total
     * @param  int|null  $places
     * @param  bool  $unsigned
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function double($column, $total = null, $places = null, $unsigned = false)
    {
    }

    /**
     * Create a new decimal column on the table.
     *
     * @param  string  $column
     * @param  int  $total
     * @param  int  $places
     * @param  bool  $unsigned
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function decimal($column, $total = 8, $places = 2, $unsigned = false)
    {
    }

    /**
     * Create a new unsigned float column on the table.
     *
     * @param  string  $column
     * @param  int  $total
     * @param  int  $places
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function unsignedFloat($column, $total = 8, $places = 2)
    {
    }

    /**
     * Create a new unsigned double column on the table.
     *
     * @param  string  $column
     * @param  int  $total
     * @param  int  $places
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function unsignedDouble($column, $total = null, $places = null)
    {
    }

    /**
     * Create a new unsigned decimal column on the table.
     *
     * @param  string  $column
     * @param  int  $total
     * @param  int  $places
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function unsignedDecimal($column, $total = 8, $places = 2)
    {
    }

    /**
     * Create a new boolean column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function boolean($column)
    {
    }

    /**
     * Create a new enum column on the table.
     *
     * @param  string  $column
     * @param  array  $allowed
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function enum($column, array $allowed)
    {
    }

    /**
     * Create a new set column on the table.
     *
     * @param  string  $column
     * @param  array  $allowed
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function set($column, array $allowed)
    {
    }

    /**
     * Create a new json column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function json($column)
    {
    }

    /**
     * Create a new jsonb column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function jsonb($column)
    {
    }

    /**
     * Create a new date column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function date($column)
    {
    }

    /**
     * Create a new date-time column on the table.
     *
     * @param  string  $column
     * @param  int  $precision
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function dateTime($column, $precision = 0)
    {
    }

    /**
     * Create a new date-time column (with time zone) on the table.
     *
     * @param  string  $column
     * @param  int  $precision
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function dateTimeTz($column, $precision = 0)
    {
    }

    /**
     * Create a new time column on the table.
     *
     * @param  string  $column
     * @param  int  $precision
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function time($column, $precision = 0)
    {
    }

    /**
     * Create a new time column (with time zone) on the table.
     *
     * @param  string  $column
     * @param  int  $precision
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function timeTz($column, $precision = 0)
    {
    }

    /**
     * Create a new timestamp column on the table.
     *
     * @param  string  $column
     * @param  int  $precision
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function timestamp($column, $precision = 0)
    {
    }

    /**
     * Create a new timestamp (with time zone) column on the table.
     *
     * @param  string  $column
     * @param  int  $precision
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function timestampTz($column, $precision = 0)
    {
    }

    /**
     * Add a "deleted at" timestamp for the table.
     *
     * @param  string  $column
     * @param  int  $precision
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function softDeletes($column = 'deleted_at', $precision = 0)
    {
    }

    /**
     * Add a "deleted at" timestampTz for the table.
     *
     * @param  string  $column
     * @param  int  $precision
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function softDeletesTz($column = 'deleted_at', $precision = 0)
    {
    }

    /**
     * Create a new year column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function year($column)
    {
    }

    /**
     * Create a new binary column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function binary($column)
    {
    }

    /**
     * Create a new uuid column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function uuid($column)
    {
    }

    /**
     * Create a new UUID column on the table with a foreign key constraint.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ForeignIdColumnDefinition
     */
    public function foreignUuid($column)
    {
    }

    /**
     * Create a new IP address column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function ipAddress($column)
    {
    }

    /**
     * Create a new MAC address column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function macAddress($column)
    {
    }

    /**
     * Create a new geometry column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function geometry($column)
    {
    }

    /**
     * Create a new point column on the table.
     *
     * @param  string  $column
     * @param  int|null  $srid
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function point($column, $srid = null)
    {
    }

    /**
     * Create a new linestring column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function lineString($column)
    {
    }

    /**
     * Create a new polygon column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function polygon($column)
    {
    }

    /**
     * Create a new geometrycollection column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function geometryCollection($column)
    {
    }

    /**
     * Create a new multipoint column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function multiPoint($column)
    {
    }

    /**
     * Create a new multilinestring column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function multiLineString($column)
    {
    }

    /**
     * Create a new multipolygon column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function multiPolygon($column)
    {
    }

    /**
     * Create a new multipolygon column on the table.
     *
     * @param  string  $column
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function multiPolygonZ($column)
    {
    }

    /**
     * Create a new generated, computed column on the table.
     *
     * @param  string  $column
     * @param  string  $expression
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function computed($column, $expression)
    {
    }

    /**
     * Add the proper columns for a polymorphic table.
     *
     * @param  string  $name
     * @param  string|null  $indexName
     * @return void
     */
    public function morphs($name, $indexName = null)
    {
    }

    /**
     * Add nullable columns for a polymorphic table.
     *
     * @param  string  $name
     * @param  string|null  $indexName
     * @return void
     */
    public function nullableMorphs($name, $indexName = null)
    {
    }

    /**
     * Add the proper columns for a polymorphic table using numeric IDs (incremental).
     *
     * @param  string  $name
     * @param  string|null  $indexName
     * @return void
     */
    public function numericMorphs($name, $indexName = null)
    {
        $this->string("{$name}_type");
        $this->unsignedBigInteger("{$name}_id");
        $this->index(["{$name}_type", "{$name}_id"], $indexName);
    }

    /**
     * Add nullable columns for a polymorphic table using numeric IDs (incremental).
     *
     * @param  string  $name
     * @param  string|null  $indexName
     * @return void
     */
    public function nullableNumericMorphs($name, $indexName = null)
    {
        $this->string("{$name}_type")->nullable();
        $this->unsignedBigInteger("{$name}_id")->nullable();
        $this->index(["{$name}_type", "{$name}_id"], $indexName);
    }

    /**
     * Add the proper columns for a polymorphic table using UUIDs.
     *
     * @param  string  $name
     * @param  string|null  $indexName
     * @return void
     */
    public function uuidMorphs($name, $indexName = null)
    {
        $this->string("{$name}_type");
        $this->uuid("{$name}_id");
        $this->index(["{$name}_type", "{$name}_id"], $indexName);
    }

    /**
     * Add nullable columns for a polymorphic table using UUIDs.
     *
     * @param  string  $name
     * @param  string|null  $indexName
     * @return void
     */
    public function nullableUuidMorphs($name, $indexName = null)
    {
        $this->string("{$name}_type")->nullable();
        $this->uuid("{$name}_id")->nullable();
        $this->index(["{$name}_type", "{$name}_id"], $indexName);
    }

    /**
     * Add a new index command to the blueprint.
     *
     * @param  string  $type
     * @param  string|array  $columns
     * @param  string  $index
     * @param  string|null  $algorithm
     * @return \Illuminate\Support\Fluent
     */
    protected function indexCommand($type, $columns, $index, $algorithm = null)
    {
    }

    /**
     * Create a new drop index command on the blueprint.
     *
     * @param  string  $command
     * @param  string  $type
     * @param  string|array  $index
     * @return \Illuminate\Support\Fluent
     */
    protected function dropIndexCommand($command, $type, $index)
    {
    }

    /**
     * Create a default index name for the table.
     *
     * @param  string  $type
     * @param  array  $columns
     * @return string
     */
    protected function createIndexName($type, array $columns)
    {
    }

    /**
     * Add a new column to the blueprint.
     *
     * @param  string  $type
     * @param  string  $name
     * @param  array  $parameters
     * @return \Illuminate\Database\Schema\ColumnDefinition
     */
    public function addColumn($type, $name, array $parameters = [])
    {
    }

    /**
     * Add the columns from the callback after the given column.
     *
     * @param  string  $column
     * @param  \Closure  $callback
     * @return void
     */
    public function after($column, Closure $callback)
    {
    }

    /**
     * Remove a column from the schema blueprint.
     *
     * @param  string  $name
     * @return $this
     */
    public function removeColumn($name)
    {
    }
}

/**
 * @method $this after(string $column) Place the column "after" another column (MySQL)
 * @method $this always() Used as a modifier for generatedAs() (PostgreSQL)
 * @method $this autoIncrement() Set INTEGER columns as auto-increment (primary key)
 * @method $this change() Change the column
 * @method $this charset(string $charset) Specify a character set for the column (MySQL)
 * @method $this collation(string $collation) Specify a collation for the column (MySQL/PostgreSQL/SQL Server)
 * @method $this comment(string $comment) Add a comment to the column (MySQL)
 * @method $this default(mixed $value) Specify a "default" value for the column
 * @method $this first() Place the column "first" in the table (MySQL)
 * @method $this generatedAs(string|Expression $expression = null) Create a SQL compliant identity column (PostgreSQL)
 * @method $this index(string $indexName = null) Add an index
 * @method $this nullable(bool $value = true) Allow NULL values to be inserted into the column
 * @method $this persisted() Mark the computed generated column as persistent (SQL Server)
 * @method $this primary() Add a primary index
 * @method $this spatialIndex() Add a spatial index
 * @method $this storedAs(string $expression) Create a stored generated column (MySQL/SQLite)
 * @method $this type(string $type) Specify a type for the column
 * @method $this unique(string $indexName = null) Add a unique index
 * @method $this unsigned() Set the INTEGER column as UNSIGNED (MySQL)
 * @method $this useCurrent() Set the TIMESTAMP column to use CURRENT_TIMESTAMP as default value
 * @method $this useCurrentOnUpdate() Set the TIMESTAMP column to use CURRENT_TIMESTAMP when updating (MySQL)
 * @method $this virtualAs(string $expression) Create a virtual generated column (MySQL/SQLite)
 */
class ColumnDefinition
{
    //
}
}

namespace Illuminate\Support\Facades {
class Schema extends \Illuminate\Database\Schema\Builder {}

/**
 * @method static \Illuminate\Database\ConnectionInterface connection(string $name = null)
 * @method static \Illuminate\Database\Query\Builder table(string $table, string $as = null)
 * @method static \Illuminate\Database\Query\Expression raw($value)
 * @method static array prepareBindings(array $bindings)
 * @method static array pretend(\Closure $callback)
 * @method static array select(string $query, array $bindings = [], bool $useReadPdo = true)
 * @method static bool insert(string $query, array $bindings = [])
 * @method static bool statement(string $query, array $bindings = [])
 * @method static bool unprepared(string $query)
 * @method static int affectingStatement(string $query, array $bindings = [])
 * @method static int delete(string $query, array $bindings = [])
 * @method static int transactionLevel()
 * @method static int update(string $query, array $bindings = [])
 * @method static mixed selectOne(string $query, array $bindings = [], bool $useReadPdo = true)
 * @method static mixed transaction(\Closure $callback, int $attempts = 1)
 * @method static string getDefaultConnection()
 * @method static void beginTransaction()
 * @method static void commit()
 * @method static void listen(\Closure $callback)
 * @method static void rollBack(int $toLevel = null)
 * @method static void setDefaultConnection(string $name)
 */
class DB {
    /**
     * @return \Illuminate\Database\Query\Builder
     */
    public static function query()
    {
    }

    /**
     * @return \Illuminate\Database\Query\Builder
     */
    public static function table(string $table)
    {
    }
}
}

namespace {
    class DB extends \Illuminate\Support\Facades\DB {}
    class Schema extends \Illuminate\Support\Facades\Schema {}
}
