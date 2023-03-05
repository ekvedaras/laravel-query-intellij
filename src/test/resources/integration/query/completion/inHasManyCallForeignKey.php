<?php

use \Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Customer extends Model {}

class User extends Model
{
    public function customers(): HasMany
    {
        return $this->hasMany(Customer::class, '<caret>');
    }
}
