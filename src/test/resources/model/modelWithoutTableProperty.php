<?php

namespace App {
class User extends \Illuminate\Database\Eloquent\Model
{
}
}

(new \App\User())->newQuery()->get('<caret>');