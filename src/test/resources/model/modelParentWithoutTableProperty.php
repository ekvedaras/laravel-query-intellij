<?php

namespace App {
class DemoOneButActuallyUsers extends \Illuminate\Database\Eloquent\Model {
}
class User extends DemoOneButActuallyUsers {
}
}

(new \App\User())->newQuery()->get('<caret>');
