<?php Schema::table('users', function (\Illuminate\Database\Schema\Blueprint $table) {
    $table->unique(['id', 'email', '<caret>']);
});
