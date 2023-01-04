<?php

class Customer extends \Illuminate\Database\Eloquent\Model {
}

class User extends \Illuminate\Database\Eloquent\Model {
    public function customers() : \Illuminate\Database\Eloquent\Relations\HasMany
    {
        return $this->hasMany(Customer::class);
    }
}

$user = new User();

$user->customers()->create([
    '<caret>',
]);
