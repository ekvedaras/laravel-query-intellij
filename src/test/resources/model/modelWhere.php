<?php

namespace App {
class DemoButActuallyUsers extends \Illuminate\Database\Eloquent\Model
{
    protected $table = 'users';
}

/**
 * @method static \Illuminate\Database\Eloquent\Builder|DemoButActuallyUsers where($column, $operator, $value)
 */
class DemoButActuallyUsers extends \Illuminate\Database\Eloquent\Model
}

\App\DemoButActuallyUsers::where('<caret>');
