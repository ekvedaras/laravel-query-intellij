<?php

class User extends \Illuminate\Database\Eloquent\Model {

}

User::create([
    'email' => 'email@email.com',
    '<caret>',
]);
