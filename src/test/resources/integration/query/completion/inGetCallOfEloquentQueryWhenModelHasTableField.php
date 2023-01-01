<?php

use \Illuminate\Database\Eloquent\Model;

class Admin extends Model {
    public $table = 'users';
}

Admin::newQuery()->get('<caret>');
