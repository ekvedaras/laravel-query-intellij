<?php

use \Illuminate\Database\Eloquent\Model;

class User extends Model {
}

User::upsert([], '<caret>');
