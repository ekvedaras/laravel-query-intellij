<?php

namespace App {
class DemoOneButActuallyUsers extends \Illuminate\Database\Eloquent\Model {
    protected $table = 'users';
}
class Admin extends DemoOneButActuallyUsers {
}
}

(new \App\Admin())->newQuery()->get('<caret>');
