<?php

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\HasMany;

class Customer extends Model {
}

class User extends Model {
    public function freeCustomers(): HasMany
    {
        return $this->hasMany(Customer::class);
    }

    public function payingCustomers(): HasMany
    {
        return $this->hasMany(Customer::class);
    }
}

User::getRelation('<caret>');
