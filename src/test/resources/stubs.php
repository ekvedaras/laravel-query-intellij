<?php

namespace Illuminate\Database\Query {
    use Closure;
    use DateTimeInterface;
    use Illuminate\Contracts\Support\Arrayable;
    use Illuminate\Contracts\Pagination\LengthAwarePaginator;
    use Illuminate\Contracts\Pagination\Paginator;
    use Illuminate\Support\Collection;

    /**
     * @method $this when($case, Closure $true, Closure $false) ✅
     * @method $this select(array|mixed $columns = ['*']) ✅
     * @method $this addSelect(array|mixed $column) ✅
     * @method $this from(Closure|static|string $table, string|null $as = null) ✅
     * @method $this join(string $table, Closure|string $first, string|null $operator = null, string|null $second = null, string $type = 'inner', bool $where = false) ✅
     * @method $this joinWhere(string $table, Closure|string $first, string $operator, string $second, string $type = 'inner') ✅
     * @method $this joinSub(Closure|static|string $query, string $as, Closure|string $first, string|null $operator = null, string|null $second = null, string $type = 'inner', bool $where = false) ✅
     * @method $this leftJoin(string $table, Closure|string $first, string|null $operator = null, string|null $second = null) ✅
     * @method $this leftJoinWhere(string $table, Closure|string $first, string $operator, string $second) ✅
     * @method $this leftJoinSub(Closure|static|string $query, string $as, Closure|string $first, string|null $operator = null, string|null $second = null) ✅
     * @method $this rightJoin(string $table, Closure|string $first, string|null $operator = null, string|null $second = null) ✅
     * @method $this rightJoinWhere(string $table, Closure|string $first, string $operator, string $second) ✅
     * @method $this rightJoinSub(Closure|static|string $query, string $as, Closure|string $first, string|null $operator = null, string|null $second = null) ✅
     * @method $this crossJoin(string $table, Closure|string|null $first = null, string|null $operator = null, string|null $second = null) ✅
     * @method $this where(Closure|string|array $column, mixed $operator = null, mixed $value = null, string $boolean = 'and') ✅
     * @method $this orWhere(Closure|string|array $column, mixed $operator = null, mixed $value = null) ✅
     * @method $this whereColumn(string|array $first, string|null $operator = null, string|null $second = null, string|null $boolean = 'and') ✅
     * @method $this orWhereColumn(string|array $first, string|null $operator = null, string|null $second = null) ✅
     * @method $this whereIn(string $column, mixed $values, string $boolean = 'and', bool $not = false) ✅
     * @method $this orWhereIn(string $column, mixed $values) ✅
     * @method $this whereNotIn(string $column, mixed $values, string $boolean = 'and') ✅
     * @method $this orWhereNotIn(string $column, mixed $values) ✅
     * @method $this whereIntegerInRaw(string $column, Arrayable|array $values, $boolean = 'and', $not = false) ✅
     * @method $this orWhereIntegerInRaw(string $column, Arrayable|array $values) ✅
     * @method $this whereIntegerNotInRaw(string $column, Arrayable|array $values, string $boolean = 'and') ✅
     * @method $this orWhereIntegerNotInRaw(string $column, Arrayable|array $values) ✅
     * @method $this whereNull(string|array $columns, string $boolean = 'and', bool $not = false) ✅
     * @method $this orWhereNull(string|array $column) ✅
     * @method $this whereNotNull(string|array $columns, string $boolean = 'and') ✅
     * @method $this whereBetween(string $column, array $values, string $boolean = 'and', bool $not = false) ✅
     * @method $this whereBetweenColumns(string $column, array $values, string $boolean = 'and', bool $not = false) ✅
     * @method $this orWhereBetween(string $column, array $values) ✅
     * @method $this orWhereBetweenColumns(string $column, array $values) ✅
     * @method $this whereNotBetween(string $column, array $values, string $boolean = 'and') ✅
     * @method $this whereNotBetweenColumns(string $column, array $values, string $boolean = 'and') ✅
     * @method $this orWhereNotBetween(string $column, array $values) ✅
     * @method $this orWhereNotBetweenColumns(string $column, array $values) ✅
     * @method $this orWhereNotNull(string|array $column) ✅
     * @method $this whereDate(string $column, string $operator, DateTimeInterface|string|null $value = null, string $boolean = 'and') ✅
     * @method $this orWhereDate(string $column, string $operator, DateTimeInterface|string|null $value = null) ✅
     * @method $this whereTime(string $column, string $operator, DateTimeInterface|string|null $value = null, string $boolean = 'and') ✅
     * @method $this orWhereTime(string $column, string $operator, DateTimeInterface|string|null $value = null) ✅
     * @method $this whereDay(string $column, string $operator, DateTimeInterface|string|null $value = null, string $boolean = 'and') ✅
     * @method $this orWhereDay(string $column, string $operator, DateTimeInterface|string|null $value = null) ✅
     * @method $this whereMonth(string $column, string $operator, DateTimeInterface|string|null $value = null, string $boolean = 'and') ✅
     * @method $this orWhereMonth(string $column, string $operator, DateTimeInterface|string|null $value = null) ✅
     * @method $this whereYear(string $column, string $operator, DateTimeInterface|string|null $value = null, string $boolean = 'and') ✅
     * @method $this orWhereYear(string $column, string $operator, DateTimeInterface|string|int|null $value = null) ✅
     * @method $this whereRowValues(array $columns, string $operator, array $values, string $boolean = 'and') ✅
     * @method $this orWhereRowValues(array $columns, string $operator, array $values) ✅
     * @method $this whereJsonContains(string $column, mixed $value, string $boolean = 'and', bool $not = false) ✅
     * @method $this orWhereJsonContains(string $column, mixed $value) ✅
     * @method $this whereJsonDoesntContain(string $column, mixed $value, string $boolean = 'and') ✅
     * @method $this orWhereJsonDoesntContain(string $column, mixed $value) ✅
     * @method $this whereJsonLength(string $column, mixed $operator, mixed $value = null, string $boolean = 'and') ✅
     * @method $this orWhereJsonLength(string $column, mixed $operator, mixed $value = null) ✅
     * @method $this groupBy(array|string ...$groups) ✅
     * @method $this having(string $column, string|null $operator = null, string|null $value = null, string $boolean = 'and') ✅
     * @method $this orHaving(string $column, string|null $operator = null, string|null $value = null) ✅
     * @method $this havingBetween(string $column, array $values, string $boolean = 'and', bool $not = false) ✅
     * @method $this orderBy(Closure|static|Expression|string $column, string $direction = 'asc') ✅
     * @method $this orderByDesc(string $column) ✅
     * @method $this latest(string $column = 'created_at') ✅
     * @method $this oldest(string $column = 'created_at') ✅
     * @method $this forPageBeforeId(int $perPage = 15, int|null $lastId = 0, string $column = 'id') ✅
     * @method $this forPageAfterId(int $perPage = 15, int|null $lastId = 0, string $column = 'id') ✅
     * @method $this reorder(string|null $column = null, string $direction = 'asc') ✅
     * @method mixed|static find(int|string $id, array $columns = ['*']) ✅
     * @method mixed value(string $column) ✅
     * @method Collection get(array|string $columns = ['*']) ✅
     * @method LengthAwarePaginator paginate(int $perPage = 15, array $columns = ['*'], string $pageName = 'page', int|null $page = null) ✅
     * @method Paginator simplePaginate(int $perPage = 15, array $columns = ['*'], string $pageName = 'page', int|null $page = null) ✅
     * @method int getCountForPagination(array $columns = ['*']) ✅
     * @method Collection pluck(string $column, string|nul $key = null) ✅
     * @method string implode(string $column, string $glue = '') ✅
     * @method int count(string|array $columns = '*') ✅
     * @method mixed min(string $column) ✅
     * @method mixed max(string $column) ✅
     * @method mixed sum(string $column) ✅
     * @method mixed avg(string $column) ✅
     * @method mixed average(string $column) ✅
     * @method mixed aggregate(string $function, array $columns = ['*']) ✅
     * @method float|int numericAggregate(string $function, array $columns = ['*']) ✅
     * @method bool insert(array $values) ✅
     * @method int insertOrIgnore(array $values) ✅
     * @method int insertGetId(array $values, string|null $sequence = null) ✅
     * @method int insertUsing(array $columns, Closure|static|string $query) ✅
     * @method int update(array $values) ✅
     * @method bool updateOrInsert(array $attributes, array $values = []) ✅
     * @method int increment(string $column, float|int $amount = 1, array $extra = [])  ✅
     * @method int decrement(string $column, float|int $amount = 1, array $extra = []) ✅
     * @method $this newQuery() ✅
     * @method Expression raw(mixed $value)
     */
    class Builder
    {
        /** @var array  ❌ */
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
     * @method self on(Closure|string $first, string|null $operator = null, Expression|string|null $second = null, string $boolean = 'and') ✅
     * @method self orOn(Closure|string $first, string|null $operator = null, string|null $second = null, string $boolean = 'and') ✅
     * @method self newQuery() ✅
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
    use Illuminate\Database\Eloquent\Scope;

    /**
     * @property-read HigherOrderBuilderProxy $orWhere
     *
     * @mixin QueryBuilder
     *
     * @method $this make(array $attributes = []) ✅
     * @method $this firstWhere(Closure|string|array|Expression $column, mixed $operator = null, mixed $value = null, string $boolean = 'and') ✅
     * @method $this|Model|Collection|static[]|null find($id, $columns = ['*']) ✅
     * @method Collection findMany(Arrayable|array $ids, array $columns = ['*']) ✅
     * @method $this|Model|Collection|static[] findOrFail(mixed $id, array $columns = ['*']) ✅
     * @method $this|Model findOrNew(mixed $id, array $columns = ['*']) ✅
     * @method $this|Model firstOrNew(array $attributes = [], array $values = []) ✅
     * @method $this|Model firstOrCreate(array $attributes = [], array $values = []) ✅
     * @method $this|Model updateOrCreate(array $attributes, array $values = []) ✅
     * @method $this|Model firstOrFail(array $columns = ['*']) ✅
     * @method $this|Model|mixed firstOr(Closure|array $columns = ['*'], Closure|null $callback = null) ✅
     * @method Model sole(array|string $columns = ['*']) ✅
     * @method $this[]|Model[] getModels(array|string $columns = ['*']) ✅
     * @method Relation getRelation(string $name) ✅
     * @method Model|static create(array $attributes = []) ✅
     * @method Model|static forceCreate(array $attributes = []) ✅
     * @method int upsert(array $values, array|string $uniqueBy, array|null $update = null) ✅
     * @method int increment(string|Expression $column, float|int $amount = 1, array $extra = []) ❌
     * @method int decrement(string|Expression $column, float|int $amount = 1, array $extra = []) ❌
     * @method $this with(string|array $relations, string|Closure|null $callback = null) ✅
     * @method $this without(mixed $relations) ✅
     * @method $this withOnly(mixed $relations) ✅
     * @method QueryBuilder getQuery() ✅
     * @method QueryBuilder toBase() ✅
     * @method string qualifyColumn(string|Expression $column) ✅
     */
    class Builder {}

    /**
     * @mixin Builder
     * @method static Builder query() ✅
     * @method Builder newQuery() ✅
     * @method Builder|$this newModelQuery() ✅
     * @method Builder newQueryWithoutRelationships() ✅
     * @method Builder|$this newQueryWithoutScopes() ✅
     * @method Builder newQueryWithoutScope(Scope|string $scope) ✅
     * @method Builder newQueryForRestoration(array|int $ids) ✅
     * @method Builder|$this newEloquentBuilder(QueryBuilder $query) ✅
     * @method QueryBuilder newBaseQueryBuilder() ✅
     * @method int increment(string $column, float|int $amount = 1, array $extra = []) ❌
     * @method int decrement(string $column, float|int $amount = 1, array $extra = []) ❌
     * @method int incrementOrDecrement(string $column, float|int $amount, array $extra, string $method) ❌
     */
    class Model {
        use HasRelationships;
    }
}

namespace Illuminate\Database\Eloquent\Concerns {
    use Illuminate\Database\Eloquent\Relations;

    /**
     * @method Relations\HasOne hasOne() ✅
     * @method Relations\HasOneThrough hasOneThrough() ❌ ✅
     * @method Relations\HasMany hasMany() ✅
     * @method Relations\HasManyThrough hasManyThrough() ❌ ✅
     * @method Relations\BelongsTo belongsTo() ✅
     * @method Relations\BelongsToMany belongsToMany() ❌ ✅
     * @method Relations\MorphOne morphOne() ❌
     * @method Relations\MorphTo morphTo() ❌
     * @method Relations\MorphTo morphEagerTo() ❌
     * @method Relations\MorphTo morphInstanceTo() ❌
     * @method Relations\MorphMany morphMany() ❌
     * @method Relations\MorphToMany morphToMany() ❌
     * @method Relations\MorphToMany morphedByMany() ❌
     */
    trait HasRelationships {}
}

namespace Illuminate\Database\Eloquent\Relations {
    use Illuminate\Database\Eloquent\Builder;
    use Illuminate\Database\Eloquent\Model;

    class Relation extends Builder {}
    class BelongsTo extends Relation {}
    class BelongsToMany extends Relation {}
    class HasManyThrough extends Relation {}
    class HasOneThrough extends HasManyThrough {}
    abstract class HasOneOrMany extends Relation {}
    class HasMany extends HasOneOrMany {}
    abstract class MorphOneOrMany extends HasOneOrMany {}
    class MorphMany extends MorphOneOrMany {}

    class Pivot extends Model {}
    class MorphPivot extends Pivot {}
    class MorphToMany extends BelongsToMany {}
}

namespace Illuminate\Database\Schema {
    use Closure;
    use Illuminate\Database\Eloquent\Model;
    use Illuminate\Support\Fluent;
    use Illuminate\Database\Schema\ForeignKeyDefinition;
    use Illuminate\Database\Schema\ForeignIdColumnDefinition;
    use Illuminate\Database\Schema\ColumnDefinition;

    /**
     * @method static bool createDatabase(string $name) ✅
     * @method static bool dropDatabaseIfExists(string $name) ✅
     * @method static bool hasTable(string $table) ✅
     * @method static array getColumnListing(string $table) ✅
     * @method static bool hasColumn(string $table, string $column) ✅
     * @method static bool hasColumns(string $table, array $columns) ✅
     * @method static string getColumnType(string $table, string $column) ✅
     * @method static void table(string $table, Closure $callback) ✅
     * @method static void create(string $table, Closure $callback) ✅
     * @method static void drop(string $table) ✅
     * @method static void dropIfExists(string $table) ✅
     * @method static void dropColumns(string $table, string|array $columns) ✅
     * @method static void rename(string $from, string $to) ✅
     */
    class Builder {}

    /**
     * @method Fluent dropColumn(array|mixed $columns) ✅
     * @method Fluent renameColumn(string $from, string $to) ❌
     * @method Fluent dropPrimary(string|array|null $index = null) ❌
     * @method Fluent dropUnique(string|array $index) ❌
     * @method Fluent dropIndex(string|array $index) ❌
     * @method Fluent dropSpatialIndex(string|array $index) ❌
     * @method Fluent dropForeign(string|array $index) ❌
     * @method Fluent dropConstrainedForeignId(string $column) ❌
     * @method Fluent renameIndex(string $from, string $to) ❌
     * @method void dropSoftDeletes(string $column = 'deleted_at') ✅
     * @method void dropSoftDeletesTz(string $column = 'deleted_at') ✅
     * @method void dropMorphs(string $name, string|null $indexName = null) ❌
     * @method Fluent rename(string $to) ❌
     * @method Fluent primary(string|array $columns, string|null $name = null, string|null $algorithm = null) ❌
     * @method Fluent unique(string|array $columns, string|null $name = null, string|null $algorithm = null) ❌
     * @method Fluent index(string|array $columns, string|null $name = null, string|null $algorithm = null) ❌
     * @method Fluent spatialIndex(string|array $columns, string|null $name = null) ❌
     * @method Fluent rawIndex(string $expression, string $name) ❌
     * @method ForeignKeyDefinition foreign(string|array $columns, string|null $name = null) ❌
     * @method ColumnDefinition id(string $column = 'id') ✅
     * @method ColumnDefinition increments(string $column) ✅
     * @method ColumnDefinition integerIncrements(string $column) ✅
     * @method ColumnDefinition tinyIncrements(string $column) ✅
     * @method ColumnDefinition smallIncrements(string $column) ✅
     * @method ColumnDefinition mediumIncrements(string $column) ✅
     * @method ColumnDefinition bigIncrements(string $column) ✅
     * @method ColumnDefinition char(string $column, int|null $length = null) ✅
     * @method ColumnDefinition string(string $column, int|null $length = null) ✅
     * @method ColumnDefinition text(string $column) ✅
     * @method ColumnDefinition mediumText(string $column) ✅
     * @method ColumnDefinition longText(string $column) ✅
     * @method ColumnDefinition integer(string $column, bool $autoIncrement = false, bool $unsigned = false) ✅
     * @method ColumnDefinition tinyInteger(string $column, bool $autoIncrement = false, bool $unsigned = false) ✅
     * @method ColumnDefinition smallInteger(string $column, bool $autoIncrement = false, bool $unsigned = false) ✅
     * @method ColumnDefinition mediumInteger(string $column, bool $autoIncrement = false, bool $unsigned = false) ✅
     * @method ColumnDefinition bigInteger(string $column, bool $autoIncrement = false, bool $unsigned = false) ✅
     * @method ColumnDefinition unsignedInteger(string $column, bool $autoIncrement = false) ✅
     * @method ColumnDefinition unsignedTinyInteger(string $column, bool $autoIncrement = false) ✅
     * @method ColumnDefinition unsignedSmallInteger(string $column, bool $autoIncrement = false) ✅
     * @method ColumnDefinition unsignedMediumInteger(string $column, bool $autoIncrement = false) ✅
     * @method ColumnDefinition unsignedBigInteger(string $column, bool $autoIncrement = false) ✅
     * @method ForeignIdColumnDefinition foreignId(string $column) ✅
     * @method ForeignIdColumnDefinition foreignIdFor(Model|string $model, string|null $column = null) ❌
     * @method ColumnDefinition float(string $column, int $total = 8, int $places = 2, bool $unsigned = false) ✅
     * @method ColumnDefinition double(string $column, int|null $total = null, int|null $places = null, bool $unsigned = false) ✅
     * @method ColumnDefinition decimal(string $column, int $total = 8, int $places = 2, bool $unsigned = false) ✅
     * @method ColumnDefinition unsignedFloat(string $column, int $total = 8, int $places = 2) ✅
     * @method ColumnDefinition unsignedDouble(string $column, int $total = null, int $places = null) ✅
     * @method ColumnDefinition unsignedDecimal(string $column, int $total = 8, int $places = 2) ✅
     * @method ColumnDefinition boolean(string $column) ✅
     * @method ColumnDefinition enum(string $column, array $allowed) ✅
     * @method ColumnDefinition set(string $column, array $allowed) ✅
     * @method ColumnDefinition json(string $column) ✅
     * @method ColumnDefinition jsonb(string $column) ✅
     * @method ColumnDefinition date(string $column) ✅
     * @method ColumnDefinition dateTime(string $column, int $precision = 0) ✅
     * @method ColumnDefinition dateTimeTz(string $column, int $precision = 0) ✅
     * @method ColumnDefinition time(string $column, int $precision = 0) ✅
     * @method ColumnDefinition timeTz(string $column, int $precision = 0) ✅
     * @method ColumnDefinition timestamp(string $column, int $precision = 0) ✅
     * @method ColumnDefinition timestampTz(string $column, int $precision = 0) ✅
     * @method ColumnDefinition softDeletes(string $column = 'deleted_at', int $precision = 0) ✅
     * @method ColumnDefinition softDeletesTz(string $column = 'deleted_at', int $precision = 0) ✅
     * @method ColumnDefinition year(string $column) ✅
     * @method ColumnDefinition binary(string $column) ✅
     * @method ColumnDefinition uuid(string $column) ✅
     * @method ColumnDefinition foreignUuid(string $column) ✅
     * @method ColumnDefinition ipAddress(string $column) ✅
     * @method ColumnDefinition macAddress(string $column) ✅
     * @method ColumnDefinition geometry(string $column) ✅
     * @method ColumnDefinition point(string $column, int|null $srid = null) ✅
     * @method ColumnDefinition lineString(string $column) ✅
     * @method ColumnDefinition polygon(string $column) ✅
     * @method ColumnDefinition geometryCollection(string $column) ✅
     * @method ColumnDefinition multiPoint(string $column) ✅
     * @method ColumnDefinition multiLineString(string $column) ✅
     * @method ColumnDefinition multiPolygon(string $column) ✅
     * @method ColumnDefinition multiPolygonZ(string $column) ✅
     * @method ColumnDefinition computed(string $column, string $expression) ✅
     * @method void morphs(string $name, string|null $indexName = null) ❌
     * @method void nullableMorphs(string $name, string|null $indexName = null) ❌
     * @method void numericMorphs(string $name, string|null $indexName = null) ❌
     * @method void nullableNumericMorphs(string $name, string|null $indexName = null) ❌
     * @method void uuidMorphs(string $name, string|null $indexName = null) ❌
     * @method void nullableUuidMorphs(string $name, string|null $indexName = null) ❌
     * @method ColumnDefinition addColumn(string $type, string $name, array $parameters = []) ❌
     * @method void after(string $column, Closure $callback) ❌
     * @method $this removeColumn(string $name) ❌
     */
    class Blueprint {}

    /**
     * @method $this after(string $column) Place the column "after" another column (MySQL) ❌
     * @method $this always() Used as a modifier for generatedAs() (PostgreSQL) ❌
     * @method $this autoIncrement() Set INTEGER columns as auto-increment (primary key) ❌
     * @method $this change() Change the column ❌
     * @method $this charset(string $charset) Specify a character set for the column (MySQL) ❌
     * @method $this collation(string $collation) Specify a collation for the column (MySQL/PostgreSQL/SQL Server) ❌
     * @method $this comment(string $comment) Add a comment to the column (MySQL) ❌
     * @method $this default(mixed $value) Specify a "default" value for the column ❌
     * @method $this first() Place the column "first" in the table (MySQL) ❌
     * @method $this generatedAs(string|Expression $expression = null) Create a SQL compliant identity column (PostgreSQL) ❌
     * @method $this index(string $indexName = null) Add an index ❌
     * @method $this nullable(bool $value = true) Allow NULL values to be inserted into the column ❌
     * @method $this persisted() Mark the computed generated column as persistent (SQL Server) ❌
     * @method $this primary() Add a primary index ❌
     * @method $this spatialIndex() Add a spatial index ❌
     * @method $this storedAs(string $expression) Create a stored generated column (MySQL/SQLite) ❌
     * @method $this type(string $type) Specify a type for the column ❌
     * @method $this unique(string $indexName = null) Add a unique index ❌
     * @method $this unsigned() Set the INTEGER column as UNSIGNED (MySQL) ❌
     * @method $this useCurrent() Set the TIMESTAMP column to use CURRENT_TIMESTAMP as default value ❌
     * @method $this useCurrentOnUpdate() Set the TIMESTAMP column to use CURRENT_TIMESTAMP when updating (MySQL) ❌
     * @method $this virtualAs(string $expression) Create a virtual generated column (MySQL/SQLite) ❌
     */
    class ColumnDefinition {}
}

namespace Illuminate\Support\Facades {
    use Illuminate\Database\Schema\Builder;

    class Schema extends Builder {}

    /**
     * @method static \Illuminate\Database\ConnectionInterface connection(string $name = null) ❌
     * @method static \Illuminate\Database\Query\Builder table(string $table, string $as = null) ✅
     * @method static \Illuminate\Database\Query\Builder query() ✅
     * @method static \Illuminate\Database\Query\Expression raw($value) ❌
     * @method static array prepareBindings(array $bindings) ❌
     * @method static array select(string $query, array $bindings = [], bool $useReadPdo = true) ❌
     * @method static bool insert(string $query, array $bindings = []) ❌
     * @method static bool statement(string $query, array $bindings = []) ❌
     * @method static bool unprepared(string $query) ❌
     * @method static int affectingStatement(string $query, array $bindings = []) ❌
     * @method static int delete(string $query, array $bindings = []) ❌
     * @method static int update(string $query, array $bindings = []) ❌
     * @method static mixed selectOne(string $query, array $bindings = [], bool $useReadPdo = true) ❌
     * @method static void registerDoctrineType(string $class, string $name, string $type) ❌
     * @method static void setDefaultConnection(string $name) ❌
     */
    class DB {}
}

namespace {
    class DB extends \Illuminate\Support\Facades\DB {}
    class Schema extends \Illuminate\Support\Facades\Schema {}
}

namespace Illuminate\Foundation\Testing\Concerns {
    use Illuminate\Database\Eloquent\Model;
    use Illuminate\Database\Connection;

    /**
     * @method $this assertDatabaseHas(Model|string $table, array $data, string|null $connection = null) ✅
     * @method $this assertDatabaseMissing(Model|string $table, array $data, string|null $connection = null) ✅
     * @method $this assertDatabaseCount(Model|string $table, int $count, string|null $connection = null) ✅
     * @method $this assertDatabaseEmpty(Model|string $table, string|null $connection = null) ✅
     * @method $this assertDeleted(Model|string $table, array $data = [], string|null $connection = null) ✅
     * @method $this assertSoftDeleted(Model|string $table, array $data = [], string|null $connection = null, string|null $deletedAtColumn = 'deleted_at') ✅
     * @method $this assertNotSoftDeleted(Model|string $table, array $data = [], string|null $connection = null, string|null $deletedAtColumn = 'deleted_at') ✅
     */
    trait InteractsWithDatabase {}
}

namespace Illuminate\Foundation\Testing {
    abstract class TestCase extends \PHPUnit\Framework\TestCase
    {
        use Concerns\InteractsWithDatabase;
    }
}

namespace Tests {
    use Illuminate\Foundation\Testing\TestCase as BaseTestCase;

    abstract class TestCase extends BaseTestCase {}
}
