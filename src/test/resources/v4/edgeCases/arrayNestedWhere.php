<?php

class User extends \Illuminate\Database\Eloquent\Model {

}

User::where([
    ['email', '=', 'test@email.com'],
]);
