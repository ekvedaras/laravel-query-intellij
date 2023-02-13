<?php

namespace Illuminate\Database\Query {
    use Closure;
    use DateTimeInterface;
    use Illuminate\Contracts\Support\Arrayable;
    use Illuminate\Contracts\Pagination\LengthAwarePaginator;
    use Illuminate\Contracts\Pagination\Paginator;
    use Illuminate\Support\Collection;

    /**
     * @method static when($case, Closure $true, Closure $false)
     * @method static select(array|mixed $columns = ['*'])
     * @method static addSelect(array|mixed $column)
     * @method static from(Closure|static|string $table, string|null $as = null)
     * @method static join(string $table, Closure|string $first, string|null $operator = null, string|null $second = null, string $type = 'inner', bool $where = false)
     * @method static joinWhere(string $table, Closure|string $first, string $operator, string $second, string $type = 'inner')
     * @method static joinSub(Closure|static|string $query, string $as, Closure|string $first, string|null $operator = null, string|null $second = null, string $type = 'inner', bool $where = false)
     * @method static leftJoin(string $table, Closure|string $first, string|null $operator = null, string|null $second = null)
     * @method static leftJoinWhere(string $table, Closure|string $first, string $operator, string $second)
     * @method static leftJoinSub(Closure|static|string $query, string $as, Closure|string $first, string|null $operator = null, string|null $second = null)
     * @method static rightJoin(string $table, Closure|string $first, string|null $operator = null, string|null $second = null)
     * @method static rightJoinWhere(string $table, Closure|string $first, string $operator, string $second)
     * @method static rightJoinSub(Closure|static|string $query, string $as, Closure|string $first, string|null $operator = null, string|null $second = null)
     * @method static crossJoin(string $table, Closure|string|null $first = null, string|null $operator = null, string|null $second = null)
     * @method static where(Closure|string|array $column, mixed $operator = null, mixed $value = null, string $boolean = 'and')
     * @method static orWhere(Closure|string|array $column, mixed $operator = null, mixed $value = null)
     * @method static whereColumn(string|array $first, string|null $operator = null, string|null $second = null, string|null $boolean = 'and')
     * @method static orWhereColumn(string|array $first, string|null $operator = null, string|null $second = null)
     * @method static whereIn(string $column, mixed $values, string $boolean = 'and', bool $not = false)
     * @method static orWhereIn(string $column, mixed $values)
     * @method static whereNotIn(string $column, mixed $values, string $boolean = 'and')
     * @method static orWhereNotIn(string $column, mixed $values)
     * @method static whereIntegerInRaw(string $column, Arrayable|array $values, $boolean = 'and', $not = false)
     * @method static orWhereIntegerInRaw(string $column, Arrayable|array $values)
     * @method static whereIntegerNotInRaw(string $column, Arrayable|array $values, string $boolean = 'and')
     * @method static orWhereIntegerNotInRaw(string $column, Arrayable|array $values)
     * @method static whereNull(string|array $columns, string $boolean = 'and', bool $not = false)
     * @method static orWhereNull(string $column)
     * @method static whereNotNull(string|array $columns, string $boolean = 'and')
     * @method static whereBetween(string $column, array $values, string $boolean = 'and', bool $not = false)
     * @method static whereBetweenColumns(string $column, array $values, string $boolean = 'and', bool $not = false)
     * @method static orWhereBetween(string $column, array $values)
     * @method static orWhereBetweenColumns(string $column, array $values)
     * @method static whereNotBetween(string $column, array $values, string $boolean = 'and')
     * @method static whereNotBetweenColumns(string $column, array $values, string $boolean = 'and')
     * @method static orWhereNotBetween(string $column, array $values)
     * @method static orWhereNotBetweenColumns(string $column, array $values)
     * @method static orWhereNotNull(string $column)
     * @method static whereDate(string $column, string $operator, DateTimeInterface|string|null $value = null, string $boolean = 'and')
     * @method static orWhereDate(string $column, string $operator, DateTimeInterface|string|null $value = null)
     * @method static whereTime(string $column, string $operator, DateTimeInterface|string|null $value = null, string $boolean = 'and')
     * @method static orWhereTime(string $column, string $operator, DateTimeInterface|string|null $value = null)
     * @method static whereDay(string $column, string $operator, DateTimeInterface|string|null $value = null, string $boolean = 'and')
     * @method static orWhereDay(string $column, string $operator, DateTimeInterface|string|null $value = null)
     * @method static whereMonth(string $column, string $operator, DateTimeInterface|string|null $value = null, string $boolean = 'and')
     * @method static orWhereMonth(string $column, string $operator, DateTimeInterface|string|null $value = null)
     * @method static whereYear(string $column, string $operator, DateTimeInterface|string|null $value = null, string $boolean = 'and')
     * @method static orWhereYear(string $column, string $operator, DateTimeInterface|string|int|null $value = null)
     * @method static whereRowValues(array $columns, string $operator, array $values, string $boolean = 'and')
     * @method static orWhereRowValues(array $columns, string $operator, array $values)
     * @method static whereJsonContains(string $column, mixed $value, string $boolean = 'and', bool $not = false)
     * @method static orWhereJsonContains(string $column, mixed $value)
     * @method static whereJsonDoesntContain(string $column, mixed $value, string $boolean = 'and')
     * @method static orWhereJsonDoesntContain(string $column, mixed $value)
     * @method static whereJsonLength(string $column, mixed $operator, mixed $value = null, string $boolean = 'and')
     * @method static orWhereJsonLength(string $column, mixed $operator, mixed $value = null)
     * @method static groupBy(array|string ...$groups)
     * @method static having(string $column, string|null $operator = null, string|null $value = null, string $boolean = 'and')
     * @method static orHaving(string $column, string|null $operator = null, string|null $value = null)
     * @method static havingBetween(string $column, array $values, string $boolean = 'and', bool $not = false)
     * @method static orderBy(Closure|static|Expression|string $column, string $direction = 'asc')
     * @method static orderByDesc(string $column)
     * @method static latest(string $column = 'created_at')
     * @method static oldest(string $column = 'created_at')
     * @method static forPageBeforeId(int $perPage = 15, int|null $lastId = 0, string $column = 'id')
     * @method static forPageAfterId(int $perPage = 15, int|null $lastId = 0, string $column = 'id')
     * @method static reorder(string|null $column = null, string $direction = 'asc')
     * @method mixed|static find(int|string $id, array $columns = ['*'])
     * @method mixed value(string $column)
     * @method Collection get(array|string $columns = ['*'])
     * @method LengthAwarePaginator paginate(int $perPage = 15, array $columns = ['*'], string $pageName = 'page', int|null $page = null)
     * @method Paginator simplePaginate(int $perPage = 15, array $columns = ['*'], string $pageName = 'page', int|null $page = null)
     * @method int getCountForPagination(array $columns = ['*'])
     * @method Collection pluck(string $column, string|nul $key = null)
     * @method string implode(string $column, string $glue = '')
     * @method int count(string $columns = '*')
     * @method mixed min(string $column)
     * @method mixed max(string $column)
     * @method mixed sum(string $column)
     * @method mixed avg(string $column)
     * @method mixed average(string $column)
     * @method mixed aggregate(string $function, array $columns = ['*'])
     * @method float|int numericAggregate(string $function, array $columns = ['*'])
     * @method bool insert(array $values)
     * @method int insertOrIgnore(array $values)
     * @method int insertGetId(array $values, string|null $sequence = null)
     * @method int insertUsing(array $columns, Closure|static|string $query)
     * @method int update(array $values)
     * @method bool updateOrInsert(array $attributes, array $values = [])
     * @method int increment(string $column, float|int $amount = 1, array $extra = [])
     * @method int decrement(string $column, float|int $amount = 1, array $extra = [])
     * @method static newQuery()
     * @method Expression raw(mixed $value)
     */
    class Builder
    {
        /** @var array */
        public $operators = [
            '=', '<', '>', '<=', '>=', '<>', '!=', '<=>',
            'like', 'like binary', 'not like', 'ilike',
            '&', '|', '^', '<<', '>>',
            'rlike', 'not rlike', 'regexp', 'not regexp',
            '~', '~*', '!~', '!~*', 'similar to',
            'not similar to', 'not ilike', '~~*', '!~~*',
        ];
    }

    /**
     * @method self on(Closure|string $first, string|null $operator = null, Expression|string|null $second = null, string $boolean = 'and')
     * @method self orOn(Closure|string $first, string|null $operator = null, string|null $second = null, string $boolean = 'and')
     * @method self newQuery()
     */
    class JoinClause extends Builder {}
}

namespace Illuminate\Database\Eloquent {
    use Closure;
    use Illuminate\Contracts\Support\Arrayable;
    use Illuminate\Database\Query\Expression;
    use Illuminate\Database\Query\Builder as QueryBuilder;
    use Illuminate\Database\Eloquent\Collection;
    use Illuminate\Database\Eloquent\Concerns\HasRelationships;
    use Illuminate\Database\Eloquent\Model;
    use Illuminate\Database\Eloquent\Relations\Relation;

    /**
     * @property-read HigherOrderBuilderProxy $orWhere
     *
     * @mixin QueryBuilder
     *
     * @method static make(array $attributes = [])
     * @method static firstWhere(Closure|string|array|Expression $column, mixed $operator = null, mixed $value = null, string $boolean = 'and')
     * @method static|Model|Collection|static[]|null find($id, $columns = ['*'])
     * @method Collection findMany(Arrayable|array $ids, array $columns = ['*'])
     * @method static|Model|Collection|static[] findOrFail(mixed $id, array $columns = ['*'])
     * @method static|Model findOrNew(mixed $id, array $columns = ['*'])
     * @method static|Model firstOrNew(array $attributes = [], array $values = [])
     * @method static|Model firstOrCreate(array $attributes = [], array $values = [])
     * @method static|Model updateOrCreate(array $attributes, array $values = [])
     * @method static|Model firstOrFail(array $columns = ['*'])
     * @method static|Model|mixed firstOr(Closure|array $columns = ['*'], Closure|null $callback = null)
     * @method Model sole(array|string $columns = ['*'])
     * @method static[]|Model[] getModels(array|string $columns = ['*'])
     * @method Relation getRelation(string $name)
     * @method Model|static create(array $attributes = [])
     * @method Model|static forceCreate(array $attributes = [])
     * @method int upsert(array $values, array|string $uniqueBy, array|null $update = null)
     * @method int increment(string|Expression $column, float|int $amount = 1, array $extra = [])
     * @method int decrement(string|Expression $column, float|int $amount = 1, array $extra = [])
     * @method static with(string|array $relations, string|Closure|null $callback = null)
     * @method static without(mixed $relations)
     * @method QueryBuilder getQuery()
     * @method QueryBuilder toBase()
     * @method string qualifyColumn(string|Expression $column)
     */
    class Builder {}

    /** @mixin Builder */
    class Model {
        use HasRelationships;

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
}

namespace Illuminate\Database\Eloquent\Concerns {
    use Illuminate\Database\Eloquent\Relations;

    /**
     * @method Relations\HasOne hasOne()
     * @method Relations\HasOneThrough hasOneThrough()
     * @method Relations\HasMany hasMany()
     * @method Relations\HasManyThrough hasManyThrough()
     * @method Relations\BelongsTo belongsTo()
     * @method Relations\BelongsToMany belongsToMany()
     * @method Relations\MorphOne morphOne()
     * @method Relations\MorphTo morphTo()
     * @method Relations\MorphTo morphEagerTo()
     * @method Relations\MorphTo morphInstanceTo()
     * @method Relations\MorphMany morphMany()
     * @method Relations\MorphToMany morphToMany()
     * @method Relations\MorphToMany morphedByMany()
     */
    trait HasRelationships {}
}

namespace Illuminate\Database\Eloquent\Relations {
    class Relation extends \Illuminate\Database\Eloquent\Builder {
    }

    class BelongsTo extends Relation {
    }

    class BelongsToMany extends Relation {
    }

    class HasManyThrough extends Relation {
    }

    class HasOneThrough extends HasManyThrough {
    }

    abstract class HasOneOrMany extends Relation {
    }

    class HasMany extends HasOneOrMany {
    }

    abstract class MorphOneOrMany extends HasOneOrMany {
    }

    class MorphMany extends MorphOneOrMany {
    }

    class Pivot extends \Illuminate\Database\Eloquent\Model {
    }

    class MorphPivot extends Pivot {
    }

    class MorphToMany extends BelongsToMany {
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
 * @method static \PDO getPdo()
 * @method static \Illuminate\Database\ConnectionInterface connection(string $name = null)
 * @method static \Illuminate\Database\Query\Builder table(string $table, string $as = null)
 * @method static \Illuminate\Database\Query\Builder query()
 * @method static \Illuminate\Database\Query\Expression raw($value)
 * @method static array getQueryLog()
 * @method static array prepareBindings(array $bindings)
 * @method static array pretend(\Closure $callback)
 * @method static array select(string $query, array $bindings = [], bool $useReadPdo = true)
 * @method static bool insert(string $query, array $bindings = [])
 * @method static bool logging()
 * @method static bool statement(string $query, array $bindings = [])
 * @method static bool unprepared(string $query)
 * @method static int affectingStatement(string $query, array $bindings = [])
 * @method static int delete(string $query, array $bindings = [])
 * @method static int transactionLevel()
 * @method static int update(string $query, array $bindings = [])
 * @method static mixed selectOne(string $query, array $bindings = [], bool $useReadPdo = true)
 * @method static mixed transaction(\Closure $callback, int $attempts = 1)
 * @method static string getDefaultConnection()
 * @method static void afterCommit(\Closure $callback)
 * @method static void beginTransaction()
 * @method static void commit()
 * @method static void enableQueryLog()
 * @method static void disableQueryLog()
 * @method static void flushQueryLog()
 * @method static void registerDoctrineType(string $class, string $name, string $type)
 * @method static \Illuminate\Database\Connection beforeExecuting(\Closure $callback)
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

namespace Illuminate\Foundation\Testing\Concerns {
    trait InteractsWithDatabase
    {
        /**
         * Assert that a given where condition exists in the database.
         *
         * @param  \Illuminate\Database\Eloquent\Model|string  $table
         * @param  array  $data
         * @param  string|null  $connection
         * @return $this
         */
        protected function assertDatabaseHas($table, array $data, $connection = null)
        {
        }

        /**
         * Assert that a given where condition does not exist in the database.
         *
         * @param  \Illuminate\Database\Eloquent\Model|string  $table
         * @param  array  $data
         * @param  string|null  $connection
         * @return $this
         */
        protected function assertDatabaseMissing($table, array $data, $connection = null)
        {
        }

        /**
         * Assert the count of table entries.
         *
         * @param  \Illuminate\Database\Eloquent\Model|string  $table
         * @param  int  $count
         * @param  string|null  $connection
         * @return $this
         */
        protected function assertDatabaseCount($table, int $count, $connection = null)
        {
        }

        /**
         * Assert the given record has been deleted.
         *
         * @param  \Illuminate\Database\Eloquent\Model|string  $table
         * @param  array  $data
         * @param  string|null  $connection
         * @return $this
         */
        protected function assertDeleted($table, array $data = [], $connection = null)
        {
        }

        /**
         * Assert the given record has been "soft deleted".
         *
         * @param  \Illuminate\Database\Eloquent\Model|string  $table
         * @param  array  $data
         * @param  string|null  $connection
         * @param  string|null  $deletedAtColumn
         * @return $this
         */
        protected function assertSoftDeleted($table, array $data = [], $connection = null, $deletedAtColumn = 'deleted_at')
        {
        }
    }
}

namespace Illuminate\Foundation\Testing {
    abstract class TestCase extends \PHPUnit\Framework\TestCase
    {
        use Concerns\InteractsWithDatabase;
    }
}

namespace Tests {
    use Illuminate\Foundation\Testing\TestCase as BaseTestCase;

    abstract class TestCase extends BaseTestCase {
    }
}
