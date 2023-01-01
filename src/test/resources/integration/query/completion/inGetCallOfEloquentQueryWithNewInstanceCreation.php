<?php

use \Illuminate\Database\Eloquent\Model;

class User extends Model {
}

(new User)->newQuery()->get('<caret>');
