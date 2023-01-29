<?php

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Relations\Relation;
use Illuminate\Database\Eloquent\Relations\HasOne;

class User extends Model
{
    public function customer(): HasOne
    {
        return $this->hasOne(Customer::class);
    }
}

class Customer extends Model
{
}

User::with(['customer' => function (Relation $customer) {
    $customer->where('customer.id', 1);
}])->where('<caret>');
