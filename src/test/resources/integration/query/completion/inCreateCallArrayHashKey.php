<?php

use \Illuminate\Database\Eloquent\Model;

class User extends Model {

}

User::create([
    'email' => 'email@email.com',
    '<caret>' => 1,
]);
