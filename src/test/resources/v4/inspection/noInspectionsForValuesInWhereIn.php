<?php

class User extends \Illuminate\Database\Eloquent\Model {
}

User::whereIn('id', [
    'id1',
    'id2',
])->get();
