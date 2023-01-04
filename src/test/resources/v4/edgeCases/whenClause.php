<?php

class User extends \Illuminate\Database\Eloquent\Model {

}

$args = [];

User::query()->when(Arr::get($args, 'arg1', false), function (\Illuminate\Database\Eloquent\Builder $query, $arg1) {
    return $query->whereDate('<caret>', '<=', $arg1);
});
