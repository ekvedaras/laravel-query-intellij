<?php

use \Illuminate\Database\Eloquent\Model;

class User extends Model {
}

$query = User::newQuery();

$query->get('<caret>');
