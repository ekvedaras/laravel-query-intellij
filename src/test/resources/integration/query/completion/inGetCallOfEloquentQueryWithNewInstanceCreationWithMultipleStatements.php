<?php

use \Illuminate\Database\Eloquent\Model;

class User extends Model {
}

$query = new User;

$query->newQuery()->get('<caret>');
