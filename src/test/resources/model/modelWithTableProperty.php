<?php

namespace App {
class DemoButActuallyUsers extends \Illuminate\Database\Eloquent\Model
{
    protected $table = 'users';
}
}

(new \App\DemoButActuallyUsers())->newQuery()->get('<caret>');