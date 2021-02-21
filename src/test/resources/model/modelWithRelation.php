<?php

namespace App {
class User extends \Illuminate\Database\Eloquent\Model
{
    public function customer(): \Illuminate\Database\Eloquent\Relations\Relation
    {
        return $this->hasOne(Customer::class);
    }
}

class Customer extends \Illuminate\Database\Eloquent\Model
{
}
}

(new \App\User())->newQuery()->with(['customer' => function (\Illuminate\Database\Eloquent\Relations\Relation $customer) {
    $customer->where('<caret>');
}]);