<?php

class SomeObject {
    public function __construct(
        public string $prop1,
    ) { }
}

class User extends \Illuminate\Database\Eloquent\Model {}

$id = 1;

User::create([
    'id' => $id,
    'email' => $email = new SomeObject(
        prop1: 'Val1',
    ),
]);
