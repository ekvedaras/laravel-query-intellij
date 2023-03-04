<?php

use Illuminate\Database\Eloquent\Model;


class User extends Model {
    public function scopeActive()
    {
    }

    public function scopeInActive()
    {
    }
}

User::newQueryWithoutScope('<caret>');
