<?php

use \Illuminate\Database\Eloquent\Model;
use \Illuminate\Database\Eloquent\Builder;

class User extends Model
{
    public function scopeFirstId(Builder $query): void
    {
        $query->where('<caret>');
    }
}
