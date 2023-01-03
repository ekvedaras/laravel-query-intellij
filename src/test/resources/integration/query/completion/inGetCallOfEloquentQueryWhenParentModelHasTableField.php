<?php

use \Illuminate\Database\Eloquent\Model;

class User extends Model {
    public $table = 'users';
}

class Admin extends User {
}

Admin::newQuery()->get('<caret>');
