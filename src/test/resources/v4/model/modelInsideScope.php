<?php

namespace App {
class User extends \Illuminate\Database\Eloquent\Model
{
    public function scopeFirstId(\Illuminate\Database\Eloquent\Builder $query)
    {
        return $query->where('<caret>');
    }
}
}
