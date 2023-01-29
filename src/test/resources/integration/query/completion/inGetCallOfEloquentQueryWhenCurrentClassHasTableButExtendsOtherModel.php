<?php

use \Illuminate\Database\Eloquent\Model;

class User extends Model {
}

class Customer extends User {
}

Customer::newQuery()->get('<caret>');
