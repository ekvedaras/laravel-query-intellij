<?php

namespace App {
/**
 * @method static \Illuminate\Database\Eloquent\Builder|DemoButActuallyUsers where($column, $operator, $value)
 */
class User extends \Illuminate\Database\Eloquent\Model
{
}
}

\App\User::where('<caret>');
